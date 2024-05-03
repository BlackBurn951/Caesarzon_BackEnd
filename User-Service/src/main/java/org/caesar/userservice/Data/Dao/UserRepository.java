package com.example.userservice.Data.Dao;

import com.example.userservice.Data.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
