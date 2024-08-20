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
        //Fase di validazione sul servizio delle notifiche
        UUID banId= callCenter.validateBan(banDTO);

        if(banId!=null) {

            //Fase di completamento su i due servizi
            boolean localBanCompleted= adminService.completeBanOrSban(banDTO.getUserUsername(), true),
                    remoteBanCompleted= callCenter.completeBan(banId);

            if(localBanCompleted && remoteBanCompleted) {

                //Fase di rilascio dei lock su i due servizi
                adminService.releaseLock(banDTO.getUserUsername());
                callCenter.releaseLock(banId);
            }
                return adminService.completeBanOrSban(banDTO.getUserUsername());  //TODO DA POTER AGGIUNGERE ROLLBACK PER SERVIZIO NOTIFICHE
        }
        adminService.rollbackBanOrSban(banDTO.getUserUsername(), false);
        return false;
    }

    public boolean processSban(String username) {
        if(callCenter.validateSban(username)) {
            if(callCenter.completeSban(username))
                return adminService.completeBanOrSban(username);
        }
        adminService.rollbackBanOrSban(username, true);
        return false;
    }
}
