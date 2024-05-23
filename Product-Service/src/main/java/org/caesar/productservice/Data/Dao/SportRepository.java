package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Long> {
}
