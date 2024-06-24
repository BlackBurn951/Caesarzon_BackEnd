package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.UserNotificationDTO;

import java.util.List;
import java.util.UUID;

public interface UserNotificationService {
    List<UserNotificationDTO> getUserNotification(String username);
    boolean addUserNotification(UserNotificationDTO notificationDTO);
    boolean updateUserNotification(List<UserNotificationDTO> notificationDTO);
    boolean deleteUserNotification(UUID id);

}
