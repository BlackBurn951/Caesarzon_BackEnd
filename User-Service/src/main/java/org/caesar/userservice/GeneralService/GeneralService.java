package org.caesar.userservice.GeneralService;

import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.UserSearchDTO;

import java.util.List;

public interface GeneralService {

    boolean addAddress(String userUsername, AddressDTO addressDTO);
    boolean addCard(String userUsername, CardDTO cardDTO);

    List<UserSearchDTO> getUserSearch(int start);
    CardDTO getUserCard(String userUsername, String cardName);
    AddressDTO getUserAddress(String addressName, String userUsername);
    List<String> getUserCards(String userUsername);
    List<String> getUserAddresses(String userUsername);
    List<UserSearchDTO> getFollowersOrFriend(String username, int fwl, boolean friend);

    boolean deleteUser(String username);
    boolean deleteUserAddress(String userUsername, String addressName);
    boolean deleteUserCard(String userUsername, String cardName);
}
