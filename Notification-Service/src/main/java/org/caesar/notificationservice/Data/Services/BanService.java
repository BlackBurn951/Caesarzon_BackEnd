package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.BanDTO;

import java.util.List;
import java.util.UUID;

public interface BanService {
    List<BanDTO> getAllBans(int num);
    boolean checkIfBanned(String username);

    UUID validateBan();
    boolean confirmBan(BanDTO banDTO);

    boolean releaseLock(UUID banId);
    boolean rollback(UUID banId);


    UUID validateSbanUser(String username);
    boolean completeSbanUser(String username);

}
