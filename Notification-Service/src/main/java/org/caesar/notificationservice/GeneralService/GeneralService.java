package org.caesar.notificationservice.GeneralService;

import org.caesar.notificationservice.Dto.*;

import java.util.List;
import java.util.UUID;

public interface GeneralService {
    
    boolean addReportRequest(String username1, ReportDTO reportDTO);
    boolean addSupportRequest(String username1, SupportDTO supportDTO);
    boolean manageSupportRequest(String username, UUID supportId, String explain);
    boolean manageReport(String username, UUID reviewId, boolean product, boolean accept);
    boolean updateAdminNotification(List<AdminNotificationDTO> notificationDTO);

}
