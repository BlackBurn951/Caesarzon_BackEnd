package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.UserCardDTO;

import java.util.List;

public interface UserCardService {

    boolean addUserCards(UserCardDTO userCardDTO);
    UserCardDTO getUserCard(String userId, int addressNum);
    List<String> getCards(String userid);
    List<UserCardDTO> getUserCards(String userId);
    boolean deleteUserCard(UserCardDTO userCardDTO);
    boolean deleteUserCards(String userId);

}
