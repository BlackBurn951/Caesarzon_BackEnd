package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.Report;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    @Query("SELECT COUNT(*) FROM segnala where username_utente2= :username and effettiva is true GROUP BY username_utente2 HAVING COUNT(DISTINCT id_recensione) > 1")
    int countByUsernameUser2(@Param("username") String username);

    void deleteByReviewId(UUID id);

    List<Report> findAllByReviewId(UUID id);

    List<Report> findByUsernameUser2(String username);

    Report findByUsernameUser1AndReviewIdAndEffectiveIsTrue(String usernameUser1, UUID reviewId);
}
