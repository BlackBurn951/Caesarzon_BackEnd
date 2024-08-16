package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface BanRepository extends JpaRepository<Ban, UUID> {
    Ban findByUserUsername(String Username);
}
