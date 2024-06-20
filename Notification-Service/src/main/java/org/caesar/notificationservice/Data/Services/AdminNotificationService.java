package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.AdminNotificationDTO;
import org.caesar.notificationservice.Dto.NotificationDTO;

import java.util.List;

public interface AdminNotificationService {
    List<NotificationDTO> getAdminNotification(String username);
    boolean sendNotificationAllAdmin(List<AdminNotificationDTO> notification);
    boolean deleteAdminNotification(NotificationDTO notificationDTO, String username);
}
