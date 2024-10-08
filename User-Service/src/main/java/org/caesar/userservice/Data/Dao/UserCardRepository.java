package org.caesar.userservice.Data.Dao;


import org.caesar.userservice.Data.Entities.Card;
import org.caesar.userservice.Data.Entities.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface UserCardRepository extends JpaRepository<UserCard, UUID> {

    List<UserCard> findByUserUsername(String userId);

    List<UserCard> findAllByUserUsername(String userId);

    UserCard findByUserUsernameAndId(String userId, UUID id);

    void deleteAllByUserUsername(String username);
}
