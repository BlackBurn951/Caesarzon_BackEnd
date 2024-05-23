package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
