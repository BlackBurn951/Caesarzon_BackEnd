package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface AdminRepository extends JpaRepository<Admin, UUID> {
}
