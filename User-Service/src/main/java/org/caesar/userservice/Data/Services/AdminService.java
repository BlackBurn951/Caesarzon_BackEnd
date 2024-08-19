package org.caesar.userservice.Data.Services;


import org.caesar.userservice.Dto.BanDTO;

import java.util.List;

public interface AdminService {

    List<String> getAdmins();
    int validateBan(BanDTO banDTO);
    boolean completeBan(String username);
    boolean rollbackBan(String username);
    int validateSbanUser(String username);
    List<String> getBansUser(int start);
}
