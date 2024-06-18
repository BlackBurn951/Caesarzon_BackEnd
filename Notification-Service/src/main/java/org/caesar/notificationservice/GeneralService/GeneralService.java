package org.caesar.notificationservice.GeneralService;

import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.SupportDTO;

public interface GeneralService {
    
    boolean addReportRequest(String username1, SendReportDTO reportDTO);
}
