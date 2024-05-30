package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CardDTO;

public interface CardService {
    boolean saveOrUpdateCard(CardDTO cardDTO, boolean isUpdate);
    CardDTO getCard(String cardName);

}
