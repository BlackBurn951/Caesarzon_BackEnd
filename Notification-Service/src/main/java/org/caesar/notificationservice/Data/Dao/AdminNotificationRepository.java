package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID> {
    List<AdminNotification> findAllByAdmin(String admin);

    void deleteByDateAndSubjectAndAdminAndRead(LocalDate date, String subject, String user, boolean read);

    void deleteByReportId(UUID id);
}
