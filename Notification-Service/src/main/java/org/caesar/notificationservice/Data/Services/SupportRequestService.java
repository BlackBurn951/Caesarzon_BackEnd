package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.SupportDTO;

import java.util.List;

public interface SupportRequestService {

    List<SupportDTO> getAllSupportRequest(int num);
    SupportDTO getSupport(String supportCode);
    boolean addSupportRequest(SupportDTO supportDTO);
    boolean deleteSupportRequest(SupportDTO supportDTO);
}
