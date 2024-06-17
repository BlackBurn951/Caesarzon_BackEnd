package org.caesar.notificationservice.GeneralService;

import org.caesar.notificationservice.Dto.SendReportDTO;

public interface GeneralService {
    
    boolean addReportRequest(String username1, SendReportDTO reportDTO);
}
