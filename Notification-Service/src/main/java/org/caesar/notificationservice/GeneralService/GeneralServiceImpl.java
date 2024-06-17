package org.caesar.notificationservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.SendReportDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService{

    private final RestTemplate restTemplate;
    private final ReportService reportService;

    @Override
    @Transactional
    public boolean addReportRequest(String username1, SendReportDTO reportDTO) {
        ReportDTO reportRequest= new ReportDTO();

        LocalDate date= LocalDate.now();

        reportRequest.setReportDate(date);
        reportRequest.setReason(reportRequest.getReason());
        reportRequest.setUsernameUser1(username1);
        reportRequest.setUsernameUser2(reportRequest.getUsernameUser2());

        if(reportService.addReport(reportRequest)) {
            List<String> admins= restTemplate.getForObject("http://user-service/user-api/admins", List.class);
        }
        else
            return false;
    }
}
