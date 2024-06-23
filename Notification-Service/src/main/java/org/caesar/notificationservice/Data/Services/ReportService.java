package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.ReportDTO;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    ReportDTO addReport(ReportDTO reportDTO);
    List<ReportDTO> getAllReports(int num);
    ReportDTO getReport(UUID id);
    boolean deleteReport(UUID reviewId);
    int countReportForUser(String username, UUID reviewId);
}
