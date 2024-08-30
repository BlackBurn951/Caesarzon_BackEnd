package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.ReportDTO;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    ReportDTO addReport(ReportDTO reportDTO);
    List<ReportDTO> getAllReports(int num);
    ReportDTO getReport(UUID id);

    List<ReportDTO> validateDeleteReportByReview(UUID reviewId, boolean rollback);
    boolean completeDeleteReportByReview(UUID reviewId);
    boolean releaseLock(List<UUID> reportsId);


    List<ReportDTO> validateDeleteReportByUsername2(String username, boolean rollback);
    boolean completeDeleteReportByUsername2(String username);


    ReportDTO getReportByReviewId(UUID reviewId);
    int countReportForUser(String username);
    boolean findByUsername1AndReviewId(String username1, UUID reviewId);
    boolean deleteReport(ReportDTO reportDTO);
    List<ReportDTO> getReportsByReviewId(UUID id);
    List<ReportDTO> getReportsByUsername2(String username);
}
