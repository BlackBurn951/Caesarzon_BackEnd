package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
}
