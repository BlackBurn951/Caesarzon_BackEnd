package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SportRepository extends JpaRepository<Sport, UUID> {
}
