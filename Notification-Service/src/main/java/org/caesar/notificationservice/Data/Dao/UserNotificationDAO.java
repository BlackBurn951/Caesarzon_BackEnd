package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserNotificationDAO extends JpaRepository<UserNotification, Long> {
}