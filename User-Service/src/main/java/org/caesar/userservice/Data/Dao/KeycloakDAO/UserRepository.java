package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;
import org.jboss.resteasy.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository{

    User findUserById(String id);
    List<User> findAllUsers(int start);
    List<String> findAllUsersByUsername(String username);
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    boolean saveUser(UserRegistrationDTO userRegistrationDTO);
    boolean updateUser(UserDTO userDTO);
    String getUserIdFromToken();
    boolean deleteUser(String username);
}
