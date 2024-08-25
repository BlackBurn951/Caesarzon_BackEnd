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
    boolean checkCard(String username, UUID cardId);
    boolean deleteUserCard(UserCardDTO userCardDTO);

    List<UUID> validateOrRollbackUserCardsDelete(String username, boolean rollback);
    List<CardDTO> completeUserCardsDelete(String username);
    boolean releaseLockUserCards(String username);
    boolean rollbackUserCards(String username, List<CardDTO> cards);
}
