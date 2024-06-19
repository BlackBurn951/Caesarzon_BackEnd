package org.caesar.notificationservice.Data.Dao;

import org.caesar.notificationservice.Data.Entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    void deleteByReportCode(String code);
    Report findByReportCode(String reportCode);
}
