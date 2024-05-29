package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Entities.User;

import java.util.List;

public interface AdminRepository {
    Admin findUserById(String id);
    List<Admin> findAllUsers();
    Admin findAdminByEmail(String email);
    Admin findAdminByUsername(String username);
}
