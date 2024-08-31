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
        UUID banId= callCenter.validateBan();

        if(banId!=null) {

            //Fase di completamento su i due servizi
            boolean localBanCompleted= adminService.completeBanOrSban(banDTO.getUserUsername(), true),
                    remoteBanCompleted= callCenter.completeBan(banId, banDTO);

            if(localBanCompleted && remoteBanCompleted) {

                //Fase di rilascio dei lock su i due servizi
                adminService.releaseLock(banDTO.getUserUsername());   //GESTIONE ERRORE FATTA SUL DB CON PROCEDURE FUCTION
                callCenter.releaseLock(banId);

                return true;
            }

            //Fase di rollback post completamento
            adminService.rollbackBanOrSban(banDTO.getUserUsername(), false);
            callCenter.rollback(banId);

            return false;
        }

        //Fase di rollback della validazione in locale
        adminService.releaseLock(banDTO.getUserUsername());
        return false;
    }

    public boolean processSban(String username) {

        //Fase di validazione sul servizio delle notifiche
        UUID sbanId= callCenter.validateSban(username);
        if(sbanId!=null) {

            //Fase di completamento su i due servizi
            boolean localCompletedSban= adminService.completeBanOrSban(username, false),
                    remoteCompletedSban= callCenter.completeSban(username);

            if(localCompletedSban && remoteCompletedSban) {

                //Fase di rilascio dei lock su i due servizi
                adminService.releaseLock(username);   //GESTIONE ERRORE FATTA SUL DB CON PROCEDURE FUCTION
                callCenter.releaseLock(sbanId);

                return true;
            }

            //Fase di rollback post completamento
            adminService.rollbackBanOrSban(username, true);
            callCenter.rollback(sbanId);

            return false;
        }

        //Fase di rollback della validazione in locale
        adminService.releaseLock(username);
        return false;
    }
}
