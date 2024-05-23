package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Follower;
import org.springframework.data.jpa.repository.JpaRepository;



public interface FollowerRepository extends JpaRepository<Follower, Long> {
}
