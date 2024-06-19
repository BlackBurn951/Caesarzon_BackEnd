package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.AdminNotificationDTO;

import java.util.List;

public interface AdminNotificationService {
    boolean sendNotificationAllAdmin(List<AdminNotificationDTO> notification);

    boolean deleteNotification();
}
