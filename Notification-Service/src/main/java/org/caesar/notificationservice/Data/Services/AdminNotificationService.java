package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.*;

import java.util.List;
import java.util.UUID;

public interface AdminNotificationService {
    List<AdminNotificationDTO> getAdminNotification(String username);
    boolean sendNotificationAllAdmin(List<SaveAdminNotificationDTO> notification);
    boolean deleteAdminNotification(UUID id);
    boolean deleteByReport(ReportDTO reportDTO);
    boolean deleteBySupport(SupportDTO supportDTO);
    boolean updateAdminNotification(List<SaveAdminNotificationDTO> notificationDTO);

}
