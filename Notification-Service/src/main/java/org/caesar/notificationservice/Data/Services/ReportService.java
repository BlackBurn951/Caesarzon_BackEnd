package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.ReportDTO;

import java.util.List;

public interface ReportService {
    boolean addReport(ReportDTO reportDTO);

    List<ReportDTO> getAllReports(int num);

    boolean deleteReport(ReportDTO reportDTO);
}
