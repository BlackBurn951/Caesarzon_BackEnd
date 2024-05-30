package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;

public interface UserService {

    UserDTO getUser();
    boolean saveUser(UserRegistrationDTO userData);
    boolean savePhoneNumber(PhoneNumberDTO phoneNumberDTO);
}
