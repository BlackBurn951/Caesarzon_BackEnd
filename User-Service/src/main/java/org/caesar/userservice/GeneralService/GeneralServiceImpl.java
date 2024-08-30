package org.caesar.userservice.GeneralService;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
import org.caesar.userservice.Sagas.BanOrchestrator;
import org.caesar.userservice.Sagas.DeleteOrchestrator;
import org.caesar.userservice.Utils.Utils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.*;


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
    private final RestTemplate restTemplate;
    private final BanOrchestrator banOrchestrator;
    private final DeleteOrchestrator deleteOrchestrator;

    private final static String DELETE_SERVICE = "deleteService";


    private boolean fallbackCircuitBreaker(Throwable e){
        log.info("Servizio per l'elimazione non disponibile");
        return false;
    }



    //Metodo per agggiungere un utente
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

    //Metodo per aggiungere un indirizzo
    @Override
    @Transactional
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
    public boolean validatePayment(String username, UUID cardId, double total, boolean rollback) {
        if(userCardService.checkCard(username, cardId)) {
            CardDTO cardDTO= cardService.getCard(userCardService.getUserCard(cardId).getCardId());

            if(rollback) {
                cardDTO.setOnChanges(false);

                return cardService.addCard(cardDTO)!=null;
            }

            if(cardDTO.getBalance()>=total) {
                cardDTO.setOnChanges(true);

                return cardService.addCard(cardDTO)!=null;
            }
        }

        return false;
    }

    @Override
    public boolean completePayment(String username, UUID cardId, double total) {
        if(userCardService.checkCard(username, cardId)) {
            CardDTO cardDTO= cardService.getCard(userCardService.getUserCard(cardId).getCardId());

            double balance= cardDTO.getBalance();
            if(balance>=total) {
                cardDTO.setBalance(balance-total);

                return cardService.addCard(cardDTO)!=null;
            }
        }

        return false;
    }

    @Override
    public boolean releaseLockPayment(String username, UUID cardId) {
        if(userCardService.checkCard(username, cardId)) {
            CardDTO cardDTO= cardService.getCard(userCardService.getUserCard(cardId).getCardId());

            cardDTO.setOnChanges(false);

            return cardService.addCard(cardDTO)!=null;
        }

        return false;
    }

    @Override
    public boolean rollbackPayment(String username, UUID cardId, double total) {
        if(userCardService.checkCard(username, cardId)) {
            CardDTO cardDTO= cardService.getCard(userCardService.getUserCard(cardId).getCardId());

            double balance= cardDTO.getBalance();


            balance= balance+total;
            cardDTO.setBalance(balance);
            cardDTO.setOnChanges(false);

            return cardService.addCard(cardDTO)!=null;
        }
        return false;
    }



    @Override
    public boolean validateAndReleasePaymentForReturn(String username, UUID cardId, boolean rollback) {
        if(userCardService.checkCard(username, cardId)) {
            CardDTO cardDTO= cardService.getCard(userCardService.getUserCard(cardId).getCardId());

            cardDTO.setOnChanges(!rollback);

            return cardService.addCard(cardDTO)!=null;
        }

        return false;
    }

    @Override
    public boolean completeOrRollbackPaymentForReturn(String username, UUID cardId, double total, boolean rollback) {
        if(userCardService.checkCard(username, cardId)) {
            CardDTO cardDTO= cardService.getCard(userCardService.getUserCard(cardId).getCardId());

            double balance= cardDTO.getBalance();

            if(rollback)
                cardDTO.setBalance(balance-total);
            else
                cardDTO.setBalance(balance+total);

            return cardService.addCard(cardDTO)!=null;
        }

        return false;
    }



    @Override
    public String recoveryPassword(String username) {
        UserDTO user= userService.getUser(username);

        if(user==null)
            return "Utente non trovato...";

        String otp= generateOTP();
        if(utils.emailSender(username, user.getEmail(), otp)) {
            user.setOtp(otp);

            if(userService.updateUser(user))
                return "Inserisci il codice otp inviato all'email "+user.getEmail();
            return "Problemi nell'invio dell'otp...";
        }
        return "Problemi nell'invio dell'otp...";
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
    @CircuitBreaker(name= DELETE_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    public boolean deleteUser(String username) {
        UserDTO user= userService.validateOrRollbackDeleteUser(username, false);
        if(user!=null)
            return deleteOrchestrator.processUserDelete(user);

        return false;
    }

    @Override
    @Transactional
    public boolean deleteUserAddress(UUID id) {
        //Presa della tupla di relazione dell'indirizzo richiesto
        UserAddressDTO userAddressDTO= userAddressService.getUserAddress(id);

        //Controllo che la tupla di relazione esista e eliminazione dell'indirizzo associato più controllo della riuscita dell'operazione
        if(userAddressDTO!=null && userAddressService.deleteUserAddress(userAddressDTO))
            return addressService.deleteAddress(userAddressDTO.getAddressId());

        return false;
    }

    @Override
    @Transactional
    public boolean deleteUserCard(UUID id) {
        //Presa della tupla di relazione della carta richiesta
        UserCardDTO userCardDTO= userCardService.getUserCard(id);

        //Controllo che la tupla di relazione esista e eliminazione della carta associata più controllo della riuscita dell'operazione
        if(userCardDTO!=null && userCardService.deleteUserCard(userCardDTO))
            return cardService.deleteCard(userCardDTO.getCardId());

        return false;
    }


    @Override
    public int banUser(BanDTO banDTO) {
        int result= adminService.validateBan(banDTO.getUserUsername());
        if(result==0) {
            if(banOrchestrator.processBan(banDTO))
                return 0;
            return 2;
        }
        return result;
    }

    @Override
    public int sbanUser(String username) {
        int result= adminService.validateSbanUser(username);
        if(result==0) {
            if(banOrchestrator.processSban(username))
                return 0;
            return 2;
        }
        return result;
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
