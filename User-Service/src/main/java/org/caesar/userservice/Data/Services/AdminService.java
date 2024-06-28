package org.caesar.userservice.Data.Services;


import org.caesar.userservice.Dto.BanDTO;

import java.util.List;

public interface AdminService {

    List<String> getAdmins();
    boolean banUser(BanDTO banDTO);
    boolean sbanUser(String username);
    List<String> getBansUser(int start);
}
