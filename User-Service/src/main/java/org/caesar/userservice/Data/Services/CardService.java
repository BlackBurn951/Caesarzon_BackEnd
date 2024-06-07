package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.CardDTO;

public interface CardService {
    boolean saveCard(CardDTO cardDTO);
    CardDTO getCard(String cardName);
    boolean deleteCard(String cardName);
    boolean deleteUserCards(String userId);
}
