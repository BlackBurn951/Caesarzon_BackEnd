package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Dto.*;

import java.util.List;

public interface UserRepository{

    User findUserById(String id);
    List<User> findAllUsers(int start);
    List<String> findAllUsersByUsername(String username);
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    boolean saveUser(UserRegistrationDTO userRegistrationDTO);
    boolean updateUser(UserDTO userDTO);
    boolean banUser(String username, boolean ban);
    boolean deleteUser(String username);

    boolean changePassword(PasswordChangeDTO passwordChangeDTO, String username);

}
