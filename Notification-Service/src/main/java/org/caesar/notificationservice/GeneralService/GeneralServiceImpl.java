package org.caesar.notificationservice.GeneralService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Dao.AdminNotificationRepository;
import org.caesar.notificationservice.Data.Services.AdminNotificationService;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.AdminNotificationDTO;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.SendReportDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService{

    private final RestTemplate restTemplate;
    private final ReportService reportService;
    private final AdminNotificationService adminNotificationService;


    @Override
    @Transactional
    public boolean addReportRequest(String username1, SendReportDTO reportDTO) {
        ReportDTO reportRequest= new ReportDTO();

        LocalDate date= LocalDate.now();

        reportRequest.setReportDate(date);
        reportRequest.setReason(reportRequest.getReason());
        reportRequest.setDescription(reportDTO.getDescrizione());
        reportRequest.setUsernameUser1(username1);
        reportRequest.setUsernameUser2(reportRequest.getUsernameUser2());

        if(reportService.addReport(reportRequest)) {
            List<String> admins= restTemplate.getForObject("http://user-service/user-api/admins", List.class);

            if(admins==null)
                return false;

            List<AdminNotificationDTO> notifications= new Vector<>();
            AdminNotificationDTO notify;

            for(String ad: admins) {
                notify= new AdminNotificationDTO();
                notify.setData(date);
                notify.setDescription("L'utente "+username1+" ha mandato una segnalazione");
                notify.setAdmin(ad);
                notify.setRead(false);

                notifications.add(notify);
            }

            return adminNotificationService.sendNotificationAllAdmin(notifications);
        }
        else
            return false;
    }
}
