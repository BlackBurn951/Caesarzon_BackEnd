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

    boolean checkAddress(String username, UUID addressId);
    boolean pay(String username, UUID cardId, double total);

    boolean deleteUser(String username);
    boolean deleteUserAddress(UUID id);
    boolean deleteUserCard(UUID id);
}
