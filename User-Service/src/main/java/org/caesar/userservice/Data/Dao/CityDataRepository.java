package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.CityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityDataRepository extends JpaRepository<CityData, Long> {
}
