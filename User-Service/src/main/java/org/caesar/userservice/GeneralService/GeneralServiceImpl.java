package org.caesar.userservice.GeneralService;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
import org.caesar.userservice.Utils.Utils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.Vector;


@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralServiceImpl implements GeneralService {

    private final UserService userService;
    private final AdminService adminService;
    private final ProfilePicService profilePicService;

    private final AddressService addressService;
    private final UserAddressService userAddressService;

    private final CardService cardService;
    private final UserCardService userCardService;

    private final FollowerService followerService;

    private final Utils utils;
    private final static String GENERAL_SERVICE= "generalService";


//    public void fallbackCircuitBreaker(Throwable throwable){
//        log.debug("Circuit breaker su address service da: {}", throwable.getCausingCircuitBreakerName());
//    }

    //Metodo per agggiungere un utente
    @Override
    @Transactional
//    @CircuitBreaker(name=GENERAL_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=GENERAL_SERVICE)
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

    //Metodo per aggiungere un indirizzo
    @Override
    @Transactional
//    @CircuitBreaker(name=GENERAL_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=GENERAL_SERVICE)
    public int addAddress(String userUsername, AddressDTO addressDTO) {
        List<UserAddressDTO> addresses= userAddressService.getUserAddresses(userUsername);

        if(addresses.size()<5) {
            for(UserAddressDTO address: addresses) {
                AddressDTO userAddress = addressService.getAddress(address.getAddressId());

                if (userAddress.equals(addressDTO))
                    return 1;
            }

            return createAddress(userUsername, addressDTO);
        } else if(addresses.size()==5)
            return 2;
        return 1;
    }

    @Override
    @Transactional
//    @CircuitBreaker(name=GENERAL_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=GENERAL_SERVICE)
    public int addCard(String userUsername, CardDTO cardDTO) {
        List<UserCardDTO> cards= userCardService.getUserCards(userUsername);

        if(cards.size()<5) {
            for(UserCardDTO card: cards) {
                CardDTO userCard= cardService.getCard(card.getCardId());

                if(userCard.equals(cardDTO) || userCard.getCardNumber().equals(cardDTO.getCardNumber()))
                    return 1;
            }

            return createCard(userUsername, cardDTO);
        }
        else if(cards.size()==5)
            return 2;
        return 1;

    }


    //Metodo per prendere la lista di utenti nella ricerca con username e fotoprofilo
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

    //Metodo per prendere l'utente con follower e amici
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
            //TODO AGGIUNGERE LA FOTO PROFILO
            if(followerDTO.isFriend())
                userSearchDTO.setFriend(true);
            userSearchDTO.setFollower(true);

            userSearch.add(userSearchDTO);
        }

        return userSearch;
    }

    @Override
    public List<UserSearchDTO> getBans(int start) {
        List<String> bannedUser= adminService.getBansUser(start);

        List<UserSearchDTO> userSearch= new Vector<>();
        UserSearchDTO userSearchDTO;

        for(String username: bannedUser) {
            userSearchDTO= new UserSearchDTO();

            userSearchDTO.setUsername(username);
            userSearch.add(userSearchDTO);
        }

        return userSearch;
    }

    @Override
    public List<UserSearchDTO> getAllUserForFollower(String username, int start) {  //Metodo per scaricare tutti gli utenti e sapere se sono amici o follower
        List<UserDTO> user= userService.getUsers(start);

        if(user == null || user.isEmpty())
            return null;

        List<UserSearchDTO> userSearch= new Vector<>();
        UserSearchDTO userSearchDTO;

        for(UserDTO userDTO: user) {
            if(userDTO.getUsername().equals(username))
                continue;
            userSearchDTO= new UserSearchDTO();

            FollowerDTO followerDTO= followerService.getFollower(username, userDTO.getUsername());

            //TODO AGGIUNGERE FOTO PROFILO
            userSearchDTO.setUsername(userDTO.getUsername());
            if(followerDTO != null) {
                userSearchDTO.setFollower(true);
                userSearchDTO.setFriend(followerDTO.isFriend());
            }
            else {
                userSearchDTO.setFollower(false);
                userSearchDTO.setFriend(false);
            }
            userSearch.add(userSearchDTO);
        }

        return userSearch;
    }

    @Override
    public boolean checkAddress(String username, UUID addressId) {

        return userAddressService.checkAddress(username, addressId);
    }

    @Override
    @Transactional
//    @CircuitBreaker(name=GENERAL_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=GENERAL_SERVICE)
    public boolean pay(String username, UUID cardId, double total) {
        if(userCardService.checkCard(username, cardId)) {
            CardDTO cardDTO= cardService.getCard(userCardService.getUserCard(cardId).getCardId());

            double balance= cardDTO.getBalance();

            if(balance<total)
                return false;

            cardDTO.setBalance(balance-total);
            return cardService.addCard(cardDTO)!=null;
        }
        return false;
    }

    @Override
    public boolean recoveryPassword(String username) {
        UserDTO user= userService.getUser(username);

        if(user==null)
            return false;

        String otp= generateOTP();
        if(utils.emailSender(username, user.getEmail(), otp)) {
            user.setOtp(otp);
            return userService.updateUser(user);
        }
        return false;
    }


    //Getters per prendere i dati dalle tabelle di relazione
    @Override
    public CardDTO getUserCard(UUID id) {

        UserCardDTO userCardDTO = userCardService.getUserCard(id);

        if(userCardDTO == null)
            return null;

        return cardService.getCard(userCardDTO.getCardId());
    }

    //Metodo per prendere le carte dell'utente
    @Override
    public List<UUID> getUserCards(String userUsername) {
        return userCardService.getCards(userUsername);
    }


    @Override
    public AddressDTO getUserAddress(UUID id) {
        //Presa dell'indirizzo in posizione addressNumber sulla tabella di relazione
        UserAddressDTO userAddressDTO = userAddressService.getUserAddress(id);

        if(userAddressDTO == null)
            return null;

        return addressService.getAddress(userAddressDTO.getAddressId());
    }

    //Metodo per prendere gli indirizzi dell'utente
    @Override
    public List<UUID> getUserAddresses(String userUsername) {
        return userAddressService.getAddresses(userUsername);
    }


    //Metodi di cancellazione
    @Override
    @Transactional
//    @CircuitBreaker(name=GENERAL_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=GENERAL_SERVICE)
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
//    @CircuitBreaker(name=GENERAL_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=GENERAL_SERVICE)
    public boolean deleteUserAddress(UUID id) {
        try {
            //Presa della tupla di relazione dell'indirizzo richiesto
            UserAddressDTO userAddressDTO= userAddressService.getUserAddress(id);

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
//    @CircuitBreaker(name=GENERAL_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=GENERAL_SERVICE)
    public boolean deleteUserCard(UUID id) {

        try {
            //Presa della tupla di relazione della carta richiesta
            UserCardDTO userCardDTO= userCardService.getUserCard(id);

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

    private int createCard(String userUsername, CardDTO cardDTO) {
        cardDTO.setBalance(500.0);
        UUID cardId = cardService.addCard(cardDTO);

        if(cardId == null)
            return 1;

        UserCardDTO userCardDTO= new UserCardDTO();

        userCardDTO.setCardId(cardId);
        userCardDTO.setUserUsername(userUsername);

        return userCardService.addUserCards(userCardDTO)? 0: 1;
    }
    private int createAddress(String userUsername, AddressDTO addressDTO) {
        UUID addressId= addressService.addAddress(addressDTO);

        if(addressId == null)
            return 1;

        UserAddressDTO userAddressDTO= new UserAddressDTO();

        userAddressDTO.setAddressId(addressId);
        userAddressDTO.setUserUsername(userUsername);

        return userAddressService.addUserAddreses(userAddressDTO)? 0: 1;
    }
    private String generateOTP() {
        int length = 5;

        String charset = "0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            otp.append(charset.charAt(index));
        }

        return otp.toString();
    }
}
