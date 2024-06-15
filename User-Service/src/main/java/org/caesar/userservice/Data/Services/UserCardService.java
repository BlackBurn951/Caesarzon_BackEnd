package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.UserCardDTO;

import java.util.List;

public interface UserCardService {

    boolean addUserCards(UserCardDTO userCardDTO);
    UserCardDTO getUserCard(String userUsername, int addressNum);
    List<String> getCards(String userUsername);
    List<UserCardDTO> getUserCards(String userUsername);
    boolean deleteUserCard(UserCardDTO userCardDTO);
    boolean deleteUserCards(String userUsername);

}
