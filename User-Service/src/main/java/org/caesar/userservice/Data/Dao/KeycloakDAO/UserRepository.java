package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Dto.*;

import java.util.List;

public interface UserRepository{

    List<User> findAllUsers(int start);
    List<String> findAllUsersByUsername(String username);
    User findUserByUsername(String username);
    boolean saveUser(UserRegistrationDTO userRegistrationDTO);
    boolean updateUser(UserDTO userDTO);

    User validateOrRollbackDeleteUser(String username, boolean rollback);
    boolean releaseLockDeleteUser(String username);

    boolean changePassword(PasswordChangeDTO passwordChangeDTO, String username);
    boolean logout(String username);

}
