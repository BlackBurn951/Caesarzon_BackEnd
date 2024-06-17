package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.SupportDTO;

import java.util.List;

public interface SupportRequestService {

    List<SupportDTO> getAllSupportRequest(int num);

}
