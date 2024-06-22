package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.UserNotificationDTO;

import java.util.List;
import java.util.UUID;

public interface UserNotificationService {
    List<NotificationDTO> getUserNotification(String username);
    boolean addUserNotification(UserNotificationDTO notificationDTO, String username);
    boolean deleteUserNotification(NotificationDTO notificationDTO, String username);

}
