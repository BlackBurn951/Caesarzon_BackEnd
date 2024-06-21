package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.NotificationDTO;
import org.caesar.notificationservice.Dto.UserNotificationDTO;

import java.util.List;

public interface UserNotificationService {
    List<NotificationDTO> getUserNotification(String username);
    boolean addUserNotification(UserNotificationDTO notificationDTO, String username);
    boolean deleteUserNotification(NotificationDTO notificationDTO, String username);

}
