package org.caesar.notificationservice.GeneralService;

import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.SupportDTO;

public interface GeneralService {
    
    boolean addReportRequest(String username1, ReportDTO reportDTO);
    boolean addSupportRequest(String username1, SupportDTO supportDTO);
    boolean manageSupportRequest(String username, SupportDTO supportDTO);
    boolean manageReport(String username, ReportDTO reportDTO);
}
