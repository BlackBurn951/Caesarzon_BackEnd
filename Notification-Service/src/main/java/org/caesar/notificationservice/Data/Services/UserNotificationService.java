package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.UserNotificationDTO;

public interface UserNotificationService {
    boolean addUserNotification(UserNotificationDTO notification);
}
