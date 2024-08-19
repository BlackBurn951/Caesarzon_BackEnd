package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.AdminNotification;
import org.caesar.notificationservice.Data.Entities.Report;
import org.caesar.notificationservice.Data.Entities.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID> {

    List<AdminNotification> findAllByAdmin(String admin);

    List<AdminNotification> findAllByReport(Report report);

    void deleteByReport(Report report);

    void deleteBySupport(Support support);
}
