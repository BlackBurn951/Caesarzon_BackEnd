package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID> {
}
