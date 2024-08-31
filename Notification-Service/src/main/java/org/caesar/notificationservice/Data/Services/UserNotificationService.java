package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.UserNotificationDTO;

import java.util.List;
import java.util.UUID;

public interface UserNotificationService {
    List<UserNotificationDTO> getUserNotification(String username);
    boolean addUserNotification(UserNotificationDTO notificationDTO);

    UUID validateNotification();
    boolean completeNotification(UserNotificationDTO userNotificationDTO);
    boolean releaseNotification(UUID notificationId);

    boolean validateOrRollbackDeleteUserNotifications(String username, boolean rollback);

    boolean updateUserNotification(List<UserNotificationDTO> notificationDTO);
    boolean deleteUserNotification(UUID id);
    boolean deleteAllUserNotification(String username);


}
