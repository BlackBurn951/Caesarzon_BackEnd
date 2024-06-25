package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findByUserUsername(String userUsername);

    int countByUserUsername(String userUsername);

    List<UserAddress> findAllByUserUsername(String userUsername);

}
