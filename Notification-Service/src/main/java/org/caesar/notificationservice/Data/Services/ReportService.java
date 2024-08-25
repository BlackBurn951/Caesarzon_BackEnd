package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.ReportDTO;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    ReportDTO addReport(ReportDTO reportDTO);
    List<ReportDTO> getAllReports(int num);
    ReportDTO getReport(UUID id);

    boolean validateDeleteReportByUsername2(String username);
    List<ReportDTO> completeDeleteReportByUsername2(String username);
    boolean rollbackPreCompleteByUsername2(String username);

    boolean validateDeleteReportByReview(UUID reviewId);
    List<ReportDTO> completeDeleteReportByReview(UUID reviewId);
    boolean releaseLock(List<UUID> reportsId);
    boolean rollbackPreCompleteByReview(UUID reviewId);


    List<ReportDTO>  validateDeleteReportForUserDelete(String username, boolean rollback);
    boolean completeDeleteReportForUserDelete(String username);


    ReportDTO getReportByReviewId(UUID reviewId);
    int countReportForUser(String username, UUID reviewId);
    boolean findByUsername1AndReviewId(String username1, UUID reviewId);
    boolean deleteReport(ReportDTO reportDTO);
    List<ReportDTO> getReportsByReviewId(UUID id);
    List<ReportDTO> getReportsByUsername2(String username);
}
