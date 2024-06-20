package org.caesar.notificationservice.GeneralService;

import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.ReportResponseDTO;
import org.caesar.notificationservice.Dto.SupportDTO;
import org.caesar.notificationservice.Dto.SupportResponseDTO;

public interface GeneralService {
    
    boolean addReportRequest(String username1, ReportDTO reportDTO);
    boolean addSupportRequest(String username1, SupportDTO supportDTO);
    boolean manageSupportRequest(String username, SupportResponseDTO sendSupportDTO);
    boolean manageReport(ReportResponseDTO reportResponseDTO, String username);
}
