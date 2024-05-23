package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserCardRepository extends JpaRepository<UserCard, Long> {
}
