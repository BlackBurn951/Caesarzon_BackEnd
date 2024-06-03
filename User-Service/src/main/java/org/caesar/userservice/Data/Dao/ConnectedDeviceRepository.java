package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.ConnectedDevices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectedDeviceRepository extends JpaRepository<ConnectedDevices, Long> {
}
