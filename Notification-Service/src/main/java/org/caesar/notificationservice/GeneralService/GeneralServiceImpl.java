package org.caesar.notificationservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.SendReportDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService{

    private final RestTemplate restTemplate;

    @Transactional
    public boolean addReportRequest(String username1, SendReportDTO reportDTO) {
        ReportDTO reportRequest= new ReportDTO();



        reportRequest.setReportDate();
        reportRequest.setReason();
        reportRequest.setUsernameUser1();
        reportRequest.setUsernameUser2();
    }
}
