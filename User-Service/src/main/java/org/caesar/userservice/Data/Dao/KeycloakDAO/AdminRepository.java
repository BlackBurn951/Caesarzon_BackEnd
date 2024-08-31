package org.caesar.userservice.Data.Dao.KeycloakDAO;

import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Entities.User;

import java.util.List;

public interface AdminRepository {

    List<Admin> findAllAdmin();

    int validateBanUser(String username, boolean ban);

    boolean completeBanUser(String username, boolean ban);

    boolean releaseUserLock(String username);

    boolean rollbackBan(String username, boolean ban);

    User findUserByUsername(String username);

    List<User> findAllBanUsers(int start);
}


