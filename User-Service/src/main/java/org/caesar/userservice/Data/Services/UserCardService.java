package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.UserCardDTO;

public interface UserCardService {

    boolean addUserCards(UserCardDTO userCardDTO);

    UserCardDTO getUserCard(String userId, int addressNum);

}
