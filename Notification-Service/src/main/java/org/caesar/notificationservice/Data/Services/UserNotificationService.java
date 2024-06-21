package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.NotificationDTO;

import java.util.List;

public interface UserNotificationService {
    List<NotificationDTO> getUserNotification(String username);
    boolean addUserNotification(NotificationDTO notificationDTO, String username);
    boolean deleteUserNotification(NotificationDTO notificationDTO, String username);

}
