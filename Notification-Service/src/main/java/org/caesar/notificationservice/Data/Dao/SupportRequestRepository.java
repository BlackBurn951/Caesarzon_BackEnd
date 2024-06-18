package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, UUID> {

    void deleteByDateRequestAndTypeAndTextAndSubjectAndUsername(
            LocalDate dateRequest, String type, String text, String subject, String username);
}
