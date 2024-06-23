package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.BanDTO;

import java.util.List;

public interface BanService {
    List<BanDTO> getAllBans();
    boolean banUser(BanDTO banDTO);
    boolean sbanUser(String username);
}
