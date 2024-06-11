package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.UserCardDTO;

import java.util.List;
import java.util.UUID;

public interface CardService {
    boolean saveCard(CardDTO cardDTO);
    CardDTO getCard(String cardName);
    boolean deleteCard(UUID cardId);
    boolean deleteUserCards(String userId, List<UserCardDTO> userCards);
}
