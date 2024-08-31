package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.Support;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SupportRequestRepository extends JpaRepository<Support, UUID> {
    List<Support> findAllByUsername(String username);
    void deleteAllByUsername(String username);
}
