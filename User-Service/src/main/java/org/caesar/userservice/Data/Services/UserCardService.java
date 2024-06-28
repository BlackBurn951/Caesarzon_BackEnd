package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.UserCardDTO;

import java.util.List;
import java.util.UUID;

public interface UserCardService {

    boolean addUserCards(UserCardDTO userCardDTO);
    UserCardDTO getUserCard(UUID id);
    List<UUID> getCards(String userUsername);
    List<UserCardDTO> getUserCards(String userUsername);
    boolean checkCard(String username, CardDTO cardDTO);
    boolean deleteUserCard(UserCardDTO userCardDTO);
    boolean deleteUserCards(String userUsername);

}
