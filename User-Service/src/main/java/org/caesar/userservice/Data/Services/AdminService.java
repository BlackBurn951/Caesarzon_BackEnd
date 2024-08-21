package org.caesar.userservice.Data.Services;


import org.caesar.userservice.Dto.BanDTO;

import java.util.List;

public interface AdminService {

    List<String> getAdmins();
    int validateBan(String username);
    boolean completeBanOrSban(String username, boolean ban);
    boolean rollbackBanOrSban(String username, boolean ban);
    int validateSbanUser(String username);
    boolean releaseLock(String username);
    List<String> getBansUser(int start);
}
