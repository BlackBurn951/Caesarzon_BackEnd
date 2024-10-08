package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
}
