package org.caesar.userservice.Data.Dao;

import org.caesar.userservice.Data.Entities.ProfilePic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfilePicRepository extends JpaRepository<ProfilePic, UUID> {

        ProfilePic findByUserUsername(String userUsername);
        void deleteByUserUsername(String userUsername);
}
