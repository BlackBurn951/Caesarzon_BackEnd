package org.caesar.userservice.Data.Services;


import org.caesar.userservice.Dto.BanDTO;

import java.util.List;

public interface AdminService {

    List<String> getAdmins();
    int validateBan(BanDTO banDTO);
    boolean completeBan(String username);
    void rollbackBan(String username, boolean ban);
    int validateSbanUser(String username);
    List<String> getBansUser(int start);
}
