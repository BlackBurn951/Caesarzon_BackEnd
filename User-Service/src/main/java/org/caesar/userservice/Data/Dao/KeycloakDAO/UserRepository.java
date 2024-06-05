package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;

import java.util.List;

public interface UserRepository {

    User findUserById(String id);
    List<User> findAllUsers();
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    boolean saveUser(UserRegistrationDTO userRegistrationDTO);
    boolean updateUser(UserDTO userDTO);
    boolean savePhoneNumber(PhoneNumberDTO phoneNumberDTO);
}
