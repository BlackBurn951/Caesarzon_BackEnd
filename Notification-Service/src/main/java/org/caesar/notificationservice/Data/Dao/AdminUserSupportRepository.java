package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.AdminUserSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminUserSupportRepository extends JpaRepository<AdminUserSupport, UUID> {
}
