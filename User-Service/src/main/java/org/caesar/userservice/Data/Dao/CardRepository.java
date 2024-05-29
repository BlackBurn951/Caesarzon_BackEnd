package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
}
