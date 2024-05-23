package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Ban;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BanRepository extends JpaRepository<Ban, Long> {

}
