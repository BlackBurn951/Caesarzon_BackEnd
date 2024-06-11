package org.caesar.userservice.GeneralService;

import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CardDTO;

import java.util.List;

public interface GeneralService {

    boolean addAddress(AddressDTO addressDTO);
    boolean addCard(CardDTO cardDTO);
    CardDTO getUserCard(String cardName);
    AddressDTO getUserAddress(String addressName);
    List<String> getUserCards();
    List<String> getUserAddresses();
    boolean deleteUser();
    boolean deleteUserAddress(String addressName);
    boolean deleteUserCard(String cardName);
}
