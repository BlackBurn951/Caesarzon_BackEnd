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

    private final AddressService addressService;
    private final UserAddressService userAddressService;

    private final CardService cardService;
    private final UserCardService userCardService;

    private final ProfilePicService profilePicService;

    private final FollowerService followerService;

    //Metodi di inserimento dati con tabelle di relazione
    @Override
    @Transactional
    public boolean addAddress(String userUsername, AddressDTO addressDTO) {
        UUID addressId= addressService.addAddress(addressDTO);

        log.debug("Nel general service dopo aver preso l'adddressssssss");
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
    public List<UserSearchDTO> getUserSearch(String username, int start) {
        List<UserDTO> users= userService.getUsers(start);

        if(users == null || users.isEmpty())
            return null;

        List<UserSearchDTO> userSearchDTOs= new Vector<>();

        try {
            byte[] image;
            UserSearchDTO userSearchDTO= new UserSearchDTO();
            FollowerDTO followerDTO;

            for (UserDTO userDTO : users) {
                image= profilePicService.getUserImage(userDTO.getUsername());

                userSearchDTO.setUsername(userDTO.getUsername());
                userSearchDTO.setProfilePic(image);

                followerDTO= followerService.getFollower(username, userDTO.getUsername());

                if(followerDTO != null) {
                    userSearchDTO.setFriend(followerDTO.isFriend());
//                    userSearchDTO.setFollower(true);
                }

                userSearchDTOs.add(userSearchDTO);
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
            userSearchDTO.setProfilePic(profilePicService.getUserImage(followerDTO.getUserUsername2()));
            userSearchDTO.setFriend(followerDTO.isFriend());

            userSearch.add(userSearchDTO);
        }

        return userSearch;
    }


    //Getters per prendere i dati dalle tabelle di relazione
    @Override
    public CardDTO getUserCard(String userUsername, String cardName) {
        int cardNumber= getNumber(cardName);


        if(cardNumber == 0)
            return null;

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
        log.debug("uiwqdhqygdygqiqigQ");
        int addressNumber= getNumber(addressName);

        log.debug("Numero preso dal front {}", addressNumber);
        if(addressNumber == 0)
            return null;

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
    public boolean deleteUser(String username) {
        try {
            List<UserAddressDTO> userAddresses = userAddressService.getUserAddresses(username);
            List<UserCardDTO> userCards = userCardService.getUserCards(username);
            if(userAddresses==null || userAddresses.isEmpty() || userCards==null || userCards.isEmpty())
                return false;

            boolean userDeleted = userService.deleteUser(username);

            boolean userAddressesDeleted = userAddressService.deleteUserAddresses(username);
            boolean addressesDeleted = addressService.deleteAllUserAddresses(userAddresses);

            boolean userCardsDeleted = userCardService.deleteUserCards(username);
            boolean cardDeleted = cardService.deleteUserCards(userCards);

            return userDeleted && addressesDeleted && userAddressesDeleted && cardDeleted && userCardsDeleted;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione dell'utente e dei suoi dati");
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteUserAddress(String userUsername, String addressName) {
        int addressNumber= getNumber(addressName);

        if(addressNumber==0)
            return false;

        try {
            UserAddressDTO userAddressDTO= userAddressService.getUserAddress(userUsername, addressNumber);

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
        int cardNumber= getNumber(cardName);

        if(cardNumber==0)
            return false;

        try {
            UserCardDTO userCardDTO= userCardService.getUserCard(userUsername, cardNumber);

            if(userCardService.deleteUserCard(userCardDTO))
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
        Pattern pattern = Pattern.compile(".*([0-9]+)");
        Matcher matcher = pattern.matcher(name);

        int number= 0;
        if(matcher.matches())
            number = Integer.parseInt(matcher.group(1));

        return number;
    }
}
