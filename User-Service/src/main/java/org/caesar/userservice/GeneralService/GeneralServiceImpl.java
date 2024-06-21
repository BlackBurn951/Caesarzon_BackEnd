package org.caesar.userservice.GeneralService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralServiceImpl implements GeneralService {

    private final UserService userService;
    private final ProfilePicService profilePicService;

    private final AddressService addressService;
    private final UserAddressService userAddressService;

    private final CardService cardService;
    private final UserCardService userCardService;

    private final FollowerService followerService;


    @Override
    @Transactional
    public boolean addUser(UserRegistrationDTO user) {
        if(userService.saveUser(user)) {
            try {
                File file = new File("User-Service/src/main/resources/static/img/base_profile_pic.jpg");
                MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", Files.readAllBytes(file.toPath()));
                return profilePicService.saveImage(user.getUsername(), multipartFile, true);
            } catch (Exception | Error e) {
                log.debug("Errore nel salvataggio dell'user");
                return false;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public int addAddress(String userUsername, AddressDTO addressDTO) {
        List<UserAddressDTO> addresses= userAddressService.getUserAddresses(userUsername);

        if(addresses==null || addresses.isEmpty()) {
            UUID addressId= addressService.addAddress(addressDTO);

            if(addressId == null)
                return 1;

            UserAddressDTO userAddressDTO= new UserAddressDTO();

            userAddressDTO.setAddressId(addressId);
            userAddressDTO.setUserUsername(userUsername);

            return userAddressService.addUserAddreses(userAddressDTO)? 0: 1;
        } else if(addresses.size()==5)
            return 2;
        else {
            for(UserAddressDTO userAddressDTO: addresses) {
                AddressDTO address= addressService.getAddress(userAddressDTO.getAddressId());

                if(address.equals(addressDTO)) {
                    addressDTO.setId(address.getId());
                    UUID id= addressService.addAddress(addressDTO);
                    if(id == null)
                        return 1;
                    return 0;
                }
            }
            return 1;
        }
    }

    @Override
    @Transactional
    public int addCard(String userUsername, CardDTO cardDTO) {
        List<UserCardDTO> cards= userCardService.getUserCards(userUsername);

        if(cards==null || cards.isEmpty()) {
            UUID cardId = cardService.addCard(cardDTO);

            if(cardId == null)
                return 1;

            UserCardDTO userCardDTO= new UserCardDTO();

            userCardDTO.setCardId(cardId);
            userCardDTO.setUserUsername(userUsername);

            return userCardService.addUserCards(userCardDTO)? 0: 1;
        } else if(cards.size()==5)
            return 2;
        else {
            for(UserCardDTO userCardDTO: cards) {
                CardDTO card= cardService.getCard(userCardDTO.getCardId());

                if(card.equals(cardDTO)) {
                    cardDTO.setId(card.getId());
                    UUID id= cardService.addCard(cardDTO);
                    if(id == null)
                        return 1;
                    return 0;
                }
            }
            return 1;
        }
    }



    @Override
    public List<UserFindDTO> getUserFind(int start) {
        List<UserDTO> users= userService.getUsers(start);

        if(users == null || users.isEmpty())
            return null;

        List<UserFindDTO> userFindDTOs= new Vector<>();

        UserFindDTO userFindDTO;

        for (UserDTO userDTO : users) {
            userFindDTO= new UserFindDTO();

            userFindDTO.setUsername(userDTO.getUsername());
            userFindDTOs.add(userFindDTO);
        }

        return userFindDTOs;
}


    @Override
    public List<UserSearchDTO> getFollowersOrFriend(String username, int flw, boolean friend) {
        List<FollowerDTO> followers= followerService.getFollowersOrFriends(username, flw, friend);

        if(followers.isEmpty())
            return null;

        List<UserSearchDTO> userSearch= new Vector<>();
        UserSearchDTO userSearchDTO;

        for(FollowerDTO followerDTO: followers) {
            userSearchDTO= new UserSearchDTO();
            userSearchDTO.setUsername(followerDTO.getUserUsername2());
            userSearch.add(userSearchDTO);
        }

        return userSearch;
    }


    //Getters per prendere i dati dalle tabelle di relazione
    @Override
    public CardDTO getUserCard(String userUsername, String cardName) {
        //Presa del numero della carta desiderata
        int cardNumber= getNumber(cardName);


        if(cardNumber == 0)
            return null;

        //Presa della carta in posizione cardNumber sulla tabella di relazione
        UserCardDTO userCardDTO= userCardService.getUserCard(userUsername, cardNumber);

        if(userCardDTO == null)
            return null;

        return cardService.getCard(userCardDTO.getCardId());
    }

    @Override
    public List<String> getUserCards(String usernUsername) {
        return userCardService.getCards(usernUsername);
    }

    @Override
    public AddressDTO getUserAddress(String addressName, String username) {
        //Presa del numero dell'indirizzo desiderato
        int addressNumber= getNumber(addressName);

        if(addressNumber == 0)
            return null;

        //Presa dell'indirizzo in posizione addressNumber sulla tabella di relazione
        UserAddressDTO userAddressDTO= userAddressService.getUserAddress(username, addressNumber);

        if(userAddressDTO == null)
            return null;

        return addressService.getAddress(userAddressDTO.getAddressId());
    }

    @Override
    public List<String> getUserAddresses(String userUsername) {
        return userAddressService.getAddresses(userUsername);
    }


    //Metodi di cancellazione
    @Override
    @Transactional
    public boolean deleteUser(String username) {  //FIXME DA CONTROLLARE COSA TORNA IN CASO DI LISTA VUOTA SE NULL O EMPTY
        try {
            //Chiamate per prendere le tuple di relazione con gli indirizzi e le carte
            List<UserAddressDTO> userAddresses = userAddressService.getUserAddresses(username);
            List<UserCardDTO> userCards = userCardService.getUserCards(username);


            boolean userDeleted = userService.deleteUser(username);

            //Controllo dei casi in cui l'utente non abbia indirizzi e/o carte
            if(userAddresses!=null && !userAddresses.isEmpty() && userCards!=null && !userCards.isEmpty()) {
                boolean userAddressesDeleted = userAddressService.deleteUserAddresses(username);
                boolean addressesDeleted = addressService.deleteAllUserAddresses(userAddresses);

                boolean userCardsDeleted = userCardService.deleteUserCards(username);
                boolean cardDeleted = cardService.deleteUserCards(userCards);

                return userDeleted && addressesDeleted && userAddressesDeleted && cardDeleted && userCardsDeleted;
            } else if(userAddresses!=null && !userAddresses.isEmpty() && (userCards==null || userCards.isEmpty())) {
                boolean userAddressesDeleted = userAddressService.deleteUserAddresses(username);
                boolean addressesDeleted = addressService.deleteAllUserAddresses(userAddresses);

                return userDeleted && userAddressesDeleted && addressesDeleted;
            } else if((userAddresses==null || userAddresses.isEmpty()) && (userCards==null || userCards.isEmpty())) {
                boolean userCardsDeleted = userCardService.deleteUserCards(username);
                boolean cardDeleted = cardService.deleteUserCards(userCards);

                return userDeleted && userCardsDeleted && cardDeleted;
            }

            return userDeleted;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione dell'utente e dei suoi dati");
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteUserAddress(String userUsername, String addressName) {
        //Presa del numero dell'indirizzo desiderato
        int addressNumber= getNumber(addressName);

        if(addressNumber==0)
            return false;

        try {
            //Presa della tupla di relazione dell'indirizzo richiesto
            UserAddressDTO userAddressDTO= userAddressService.getUserAddress(userUsername, addressNumber);

            //Controllo che la tupla di relazione esista e eliminazione dell'indirizzo associato più controllo della riuscita dell'operazione
            if(userAddressDTO!=null && userAddressService.deleteUserAddress(userAddressDTO))
                return addressService.deleteAddress(userAddressDTO.getAddressId());

            return false;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione dell'indirizzo dell'utente");
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteUserCard(String userUsername, String cardName) {
        //Presa del numero della carta desiderata
        int cardNumber= getNumber(cardName);

        if(cardNumber==0)
            return false;

        try {
            //Presa della tupla di relazione della carta richiesta
            UserCardDTO userCardDTO= userCardService.getUserCard(userUsername, cardNumber);

            //Controllo che la tupla di relazione esista e eliminazione della carta associata più controllo della riuscita dell'operazione
            if(userCardDTO!=null && userCardService.deleteUserCard(userCardDTO))
                return cardService.deleteCard(userCardDTO.getCardId());

            return false;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della carta dell'utente");
            return false;
        }
    }


    //Metodi di servizio
    private int getNumber(String name) {
        //Creazione della regex per prendersi il numero dell'indirizzo mandato dall'utente
        Pattern pattern = Pattern.compile(".*([0-9]+)"); //TODO CHECK DELLA REGEX
        Matcher matcher = pattern.matcher(name);

        int number= 0;
        if(matcher.matches())
            number = Integer.parseInt(matcher.group(1));

        return number;
    }
}
