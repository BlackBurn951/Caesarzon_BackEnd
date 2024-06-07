package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.UserCardDTO;

import java.util.List;

public interface UserCardService {

    boolean addUserCards(UserCardDTO userCardDTO);
    UserCardDTO getUserCard(int addressNum);
    List<String> getCards();
    List<UserCardDTO> getUserCards(String userId);
    boolean deleteUserCard(UserCardDTO userCardDTO);
    boolean deleteUserCards(List<UserCardDTO> userCardDTO);

}
