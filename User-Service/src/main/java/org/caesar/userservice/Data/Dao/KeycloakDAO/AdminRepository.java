package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Entities.User;

import java.util.List;

public interface AdminRepository {

    List<Admin> findAllAdmin();

    boolean banUser(String username, boolean ban);

    User findUserByUsername(String username);
}


