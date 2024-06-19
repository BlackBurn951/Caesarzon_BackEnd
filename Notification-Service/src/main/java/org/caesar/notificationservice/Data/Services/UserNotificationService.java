package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.UserNotificationDTO;

public interface UserNotificationService {
    boolean addUserNotification(String username, String description, String explanation);
}
