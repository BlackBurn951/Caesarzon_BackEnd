package org.caesar.userservice.GeneralService;

import org.caesar.userservice.Dto.*;

import java.util.List;
import java.util.UUID;

public interface GeneralService {

    boolean addUser(UserRegistrationDTO user);
    int addAddress(String userUsername, AddressDTO addressDTO);
    int addCard(String userUsername, CardDTO cardDTO);

    List<UserFindDTO> getUserFind(int start);
    CardDTO getUserCard(UUID id);
    AddressDTO getUserAddress(UUID id);
    List<UUID> getUserCards(String userUsername);
    List<UUID> getUserAddresses(String userUsername);
    List<UserSearchDTO> getFollowersOrFriend(String username, int fwl, boolean friend);
    List<UserSearchDTO> getBans(int start);
    List<UserSearchDTO> getAllUserForFollower(String username, int start);

    boolean checkAddress(String username, UUID addressId);

    //2PC per il pagamento con carta
    boolean validatePayment(String username, UUID cardId, double total, boolean rollback);
    boolean completePayment(String username, UUID cardId, double total);
    boolean releaseLockPayment(String username, UUID cardId);
    boolean rollbackPayment(String username, UUID cardId, double total);


    //2PC per il reso
    boolean validateAndReleasePaymentForReturn(String username, UUID cardId, boolean rollback);
    boolean completeOrRollbackPaymentForReturn(String username, UUID cardId, double total, boolean rollback);


    String recoveryPassword(String username);
    int banUser(BanDTO banDTO);
    int sbanUser(String username);

    boolean deleteUser(String username);
    boolean deleteUserAddress(UUID id);
    boolean deleteUserCard(UUID id);
}
