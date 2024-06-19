package org.caesar.userservice.GeneralService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
import org.springframework.stereotype.Service;

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


    //Metodi di inserimento dati con tabelle di relazione
    @Override
    @Transactional
    public boolean addAddress(String userUsername, AddressDTO addressDTO) {
        UUID addressId= addressService.addAddress(addressDTO);

        if(addressId == null)
            return false;

        UserAddressDTO userAddressDTO= new UserAddressDTO();

        userAddressDTO.setAddressId(addressId);
        userAddressDTO.setUserUsername(userUsername);

        return userAddressService.addUserAddreses(userAddressDTO);
    }

    @Override
    @Transactional
    public boolean addCard(String userUsername, CardDTO cardDTO) {
        UUID cardId= cardService.addCard(cardDTO);

        if(cardId == null)
            return false;

        UserCardDTO userCardDTO= new UserCardDTO();

        userCardDTO.setCardId(cardId);
        userCardDTO.setUserUsername(userUsername);

        return userCardService.addUserCards(userCardDTO);
    }


    @Override
    public List<UserSearchDTO> getUserSearch(int start) {
        List<UserDTO> users= userService.getUsers(start);

        if(users == null || users.isEmpty())
            return null;

        List<UserSearchDTO> userSearchDTOs= new Vector<>();

        try {
            byte[] image;
            UserSearchDTO userSearchDTO= new UserSearchDTO();
            FollowerDTO followerDTO;

            for (UserDTO userDTO : users) {
                userSearchDTO= new UserSearchDTO();
            


                userSearchDTO.setUsername(userDTO.getUsername());

//                followerDTO= followerService.getFollower(username, userDTO.getUsername());
//
//                if(followerDTO != null) {
//                    userSearchDTO.setFriend(followerDTO.isFriend());
////                    userSearchDTO.setFollower(true);
//                }

                userSearchDTOs.add(userSearchDTO);
            }
            for(UserSearchDTO a: userSearchDTOs) {
                log.debug("utenti nella lista di ritorno {}", a.getUsername());
            }
            return userSearchDTOs;
        } catch (Exception | Error e) {
            log.debug("Errore nella presa delle foto profilo");
            return null;
        }
    }  //FIXME DA VEDERE SE SERVE A LUCA


    @Override
    public List<UserSearchDTO> getFollowersOrFriend(String username, int flw, boolean friend) {
        List<FollowerDTO> followers= followerService.getFollowersOrFriends(username, flw, friend);

        if(followers.isEmpty())
            return null;

        List<UserSearchDTO> userSearch= new Vector<>();
        UserSearchDTO userSearchDTO= new UserSearchDTO();

        for(FollowerDTO followerDTO: followers) {
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
