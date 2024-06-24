package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.Report;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    @Query("SELECT COUNT(*) FROM segnala as s WHERE s.username_utente2 = :username AND s.id_recensione != :reviewId")
    int countByUsernameUser2AndReviewId(@Param("username") String username, @Param("reviewId") UUID reviewId);

    void deleteByReviewId(UUID id);

    Report findByReviewId(UUID id);

    Report findByUsernameUser1AndReviewId(String usernameUser1, UUID reviewId);

}
