package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.SendReportDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    @Override
    public boolean aaddReport(String username1, SendReportDTO reportDTO) {

    }
}
