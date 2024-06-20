package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;

import java.util.List;


public interface UserService {

    UserDTO getUser(String username);
    List<UserDTO> getUsers(int start);
    List<String> getUsersByUsername(String username);
    boolean saveUser(UserRegistrationDTO userData);
    boolean updateUser(UserDTO userData);
    boolean banUser(String username, boolean ban);
    boolean deleteUser(String userId);
}
