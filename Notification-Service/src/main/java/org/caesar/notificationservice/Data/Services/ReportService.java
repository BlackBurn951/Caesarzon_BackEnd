package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.SendReportDTO;

public interface ReportService {
    boolean addReport(String username1, SendReportDTO reportDTO);
}
