package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
