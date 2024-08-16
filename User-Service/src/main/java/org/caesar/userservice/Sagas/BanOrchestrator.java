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

        if(banId!=null)
            return adminService.completeBan(banDTO.getUserUsername()) && callCenter.completeBan(banId);
        adminService.rollbackBan(banDTO.getUserUsername(), false);
        return false;
    }

    public boolean processSban(String username) {
        if(callCenter.validateSban(username))
            return adminService.completeBan(username) && callCenter.completeSban(username);
        adminService.rollbackBan(username, true);
        return false;
    }
}
