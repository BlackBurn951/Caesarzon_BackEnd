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
    boolean checkAddressAndCard(String username, UUID addressId, UUID cardId);

    boolean deleteUser(String username);
    boolean deleteUserAddress(UUID id);
    boolean deleteUserCard(UUID id);
}
