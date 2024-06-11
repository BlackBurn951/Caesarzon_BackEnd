package org.caesar.userservice.GeneralService;

public interface GeneralService {

    boolean deleteUser();
    boolean deleteUserAddress(String addressName);
    boolean deleteUserCard(String cardName);
}
