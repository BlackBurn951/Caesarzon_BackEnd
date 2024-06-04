package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.CityData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Vector;


@Repository
public interface CityDataRepository extends JpaRepository<CityData, Long> {

    Vector<CityData> findByCityIgnoreCaseStartingWith(@Param("citta") String citta, Pageable pageable);

    CityData findByCity(@Param("citta") String citta);

}
