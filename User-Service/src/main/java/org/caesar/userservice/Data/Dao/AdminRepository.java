package com.example.userservice.Data.Dao;

import com.example.userservice.Data.Entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
