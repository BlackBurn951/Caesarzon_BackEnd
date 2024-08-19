package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.ReportDTO;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    ReportDTO addReport(ReportDTO reportDTO);
    List<ReportDTO> getAllReports(int num);
    ReportDTO getReport(UUID id);
    boolean validateDeleteReport(UUID reviewId);
    List<ReportDTO> completeDeleteReport(UUID reviewId);
    boolean rollbackPreComplete(UUID reviewId);
    ReportDTO getReportByReviewId(UUID reviewId);
    int countReportForUser(String username, UUID reviewId);
    boolean findByUsername1AndReviewId(String username1, UUID reviewId);
}
