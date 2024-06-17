package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {
}
