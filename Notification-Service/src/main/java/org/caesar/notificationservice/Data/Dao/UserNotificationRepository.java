package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {

    List<UserNotification> findAllByUser(String username);
    void deleteAllByUser(String username);
}