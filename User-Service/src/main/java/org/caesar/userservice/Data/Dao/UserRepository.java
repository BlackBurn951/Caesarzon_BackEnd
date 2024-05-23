package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<User, Long> {
}
