package org.caesar.notificationservice.GeneralService;

import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.ReportResponseDTO;
import org.caesar.notificationservice.Dto.SupportDTO;
import org.caesar.notificationservice.Dto.SupportResponseDTO;

import java.util.UUID;

public interface GeneralService {
    
    boolean addReportRequest(String username1, ReportDTO reportDTO);
    boolean addSupportRequest(String username1, SupportDTO supportDTO);
    boolean manageSupportRequest(String username, SupportResponseDTO sendSupportDTO);
    boolean manageReport(String username, UUID reviewId, boolean product, boolean accept);
}
