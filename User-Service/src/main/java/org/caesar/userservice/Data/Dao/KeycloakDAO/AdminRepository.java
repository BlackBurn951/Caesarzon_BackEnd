package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Entities.User;

import java.util.List;

public interface AdminRepository {
    Admin findAdminById(String id);
    Admin findAdminByEmail(String email);
    Admin findAdminByUsername(String username);
    List<Admin> findAllAdmin();
}
