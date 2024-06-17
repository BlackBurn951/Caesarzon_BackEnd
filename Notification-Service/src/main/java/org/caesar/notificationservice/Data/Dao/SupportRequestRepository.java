package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, UUID> {
}
