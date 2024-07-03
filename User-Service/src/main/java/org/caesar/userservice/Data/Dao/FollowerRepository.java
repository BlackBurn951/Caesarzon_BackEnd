package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.Follower;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface FollowerRepository extends JpaRepository<Follower, UUID> {

    Follower findByUserUsername1AndUserUsername2(String username1, String username2);
    List<Follower> findAllByUserUsername1(String username1, Pageable pageable);
    List<Follower> findAllByUserUsername1AndFriend(String username1, boolean friend, Pageable pageable);
    void deleteAllByUserUsername1OrUserUsername2(String username1, String username2);
}
