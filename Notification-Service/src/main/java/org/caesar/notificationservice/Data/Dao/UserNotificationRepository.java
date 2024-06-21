package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.DocFlavor;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {
    List<UserNotification> findAllByUser(String username);

    UserNotification findByDateAndSubjectAndUserAndReadAndExplanation(LocalDate date, String subject, String user, boolean read, String explanation);

    boolean deleteByDateAndSubjectAndUserAndReadAndExplanation(String date, String subject, String user, boolean read, String explanation);
}