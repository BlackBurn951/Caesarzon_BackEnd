package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.*;

import java.util.List;
import java.util.UUID;

public interface AdminNotificationService {
    List<AdminNotificationDTO> getAdminNotification(String username);
    boolean sendNotificationAllAdmin(List<SaveAdminNotificationDTO> notification);
    boolean deleteAdminNotification(UUID id);
    boolean validateDeleteByReport(ReportDTO reportDTO);
    List<SaveAdminNotificationDTO> completeDeleteByReport(ReportDTO reportDTO);
    boolean rollbackPreComplete(ReportDTO reportDTO);
    boolean deleteBySupport(SupportDTO supportDTO);
    boolean updateAdminNotification(List<SaveAdminNotificationDTO> notificationDTO);

}
