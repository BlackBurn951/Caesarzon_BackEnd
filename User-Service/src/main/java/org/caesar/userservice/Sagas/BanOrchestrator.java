package org.caesar.userservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Services.AdminService;
import org.caesar.userservice.Dto.BanDTO;
import org.caesar.userservice.Utils.CallCenter;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BanOrchestrator {

    private final CallCenter callCenter;
    private final AdminService adminService;

    public boolean processBan(BanDTO banDTO) {
        UUID banId= callCenter.validateBan(banDTO);

        if(banId!=null) {
            if(callCenter.completeBan(banId))
                return adminService.completeBan(banDTO.getUserUsername());  //TODO DA POTER AGGIUNGERE ROLLBACK PER SERVIZIO NOTIFICHE
        }
        adminService.rollbackBan(banDTO.getUserUsername(), false);
        return false;
    }

    public boolean processSban(String username) {
        if(callCenter.validateSban(username)) {
            if(callCenter.completeSban(username))
                return adminService.completeBan(username);
        }
        adminService.rollbackBan(username, true);
        return false;
    }
}
