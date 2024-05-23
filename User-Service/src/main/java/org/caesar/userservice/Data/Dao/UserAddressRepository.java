package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
}
