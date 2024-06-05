package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserIdDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;


public interface UserService {


    UserIdDTO getUserId();
    UserDTO getUser();
    boolean saveUser(UserRegistrationDTO userData);
    boolean updateUser(UserDTO userData);
}
