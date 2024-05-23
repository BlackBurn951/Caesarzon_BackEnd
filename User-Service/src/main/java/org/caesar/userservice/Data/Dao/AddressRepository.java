package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;



public interface AddressRepository extends JpaRepository<Address, Long> {
}
