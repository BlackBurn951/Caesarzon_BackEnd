package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.SupportDTO;
import org.caesar.notificationservice.Dto.UserNotificationDTO;

import java.util.List;
import java.util.UUID;

public interface SupportRequestService {

    List<SupportDTO> getAllSupportRequest(int num);
    SupportDTO getSupport(UUID id);
    SupportDTO addSupportRequest(SupportDTO supportDTO);
    boolean deleteSupportRequest(SupportDTO supportDTO);


    List<SupportDTO> validateOrRollbackDeleteUserSupport(String username, boolean rollback);
}
