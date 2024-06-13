package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserIdDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;

import java.util.List;


public interface UserService {

    UserDTO getUser();
    List<String> getUsersByUsername(String username);
    boolean saveUser(UserRegistrationDTO userData);
    boolean updateUser(UserDTO userData);
    boolean deleteUser();
}
