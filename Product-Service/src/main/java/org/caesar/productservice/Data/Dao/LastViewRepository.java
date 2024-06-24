package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.LastView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LastViewRepository extends JpaRepository<LastView, UUID> {
}
