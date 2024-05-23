package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AdminNotificationDAO extends JpaRepository<AdminNotification, Long> {
}
