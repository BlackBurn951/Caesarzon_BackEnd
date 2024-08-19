package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.BanDTO;

import java.util.List;
import java.util.UUID;

public interface BanService {
    List<BanDTO> getAllBans();
    boolean checkIfBanned(String username);
    UUID validateBan(BanDTO banDTO);
    boolean confirmBan(UUID banId);
    boolean sbanUser(String username, boolean confirm);
    boolean rollback(String username);
}
