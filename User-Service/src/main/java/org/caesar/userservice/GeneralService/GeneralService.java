package org.caesar.userservice.GeneralService;

import org.caesar.userservice.Dto.*;

import java.util.List;

public interface GeneralService {

    boolean addUser(UserRegistrationDTO user);
    int addAddress(String userUsername, AddressDTO addressDTO);
    int addCard(String userUsername, CardDTO cardDTO);

    List<UserFindDTO> getUserFind(int start);
    CardDTO getUserCard(String userUsername, String cardName);
    AddressDTO getUserAddress(String addressName, String userUsername);
    List<String> getUserCards(String userUsername);
    List<String> getUserAddresses(String userUsername);
    List<UserSearchDTO> getFollowersOrFriend(String username, int fwl, boolean friend);

    boolean deleteUser(String username);
    boolean deleteUserAddress(String userUsername, String addressName);
    boolean deleteUserCard(String userUsername, String cardName);
}
