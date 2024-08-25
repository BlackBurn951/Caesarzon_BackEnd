package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.UserCardDTO;

import java.util.List;
import java.util.UUID;

public interface CardService {
    UUID addCard(CardDTO cardDTO);
    CardDTO getCard(UUID cardId);
    boolean deleteCard(UUID cardId);


    boolean validateOrRollbackCards(List<UUID> cardsId, boolean rollback);
    List<CardDTO> completeCards(List<UUID> cardsId);
    boolean releaseLockCards(List<UUID> cardsId);
    boolean rollbackCards(List<CardDTO> cards);
}
