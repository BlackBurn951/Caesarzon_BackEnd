package org.caesar.notificationservice.GeneralService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Services.AdminNotificationService;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Data.Services.SupportRequestService;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.caesar.notificationservice.Dto.AdminNotificationDTO;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.SupportDTO;
import org.caesar.notificationservice.Dto.UserNotificationDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService{

    private final RestTemplate restTemplate;
    private final ReportService reportService;
    private final SupportRequestService supportRequestService;
    private final AdminNotificationService adminNotificationService;


    @Override
    @Transactional
    public boolean addReportRequest(String username1, ReportDTO reportDTO) {
        ReportDTO reportRequest= new ReportDTO();

        LocalDate date= LocalDate.now();

        reportRequest.setReportDate(date);
        reportRequest.setReason(reportDTO.getReason());
        reportRequest.setDescription(reportDTO.getDescription());
        reportRequest.setUsernameUser1(username1);
        reportRequest.setUsernameUser2(reportDTO.getUsernameUser2());

        if(reportService.addReport(reportRequest)) {
            System.out.println("Sono prima della chiamata rest template");
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            System.out.println("TOKEN: " + request.getHeader("Authorization"));
            headers.add("Authorization", request.getHeader("Authorization"));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    "http://user-service/user-api/admins",
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            List<String> admins = responseEntity.getBody();

            //List<String> admins = restTemplate.getForObject("http://user-service/user-api/admins", List.class);


            if(admins==null)
                return false;

            List<AdminNotificationDTO> notifications= new Vector<>();
            AdminNotificationDTO notify;

            for(String ad: admins) {
                notify= new AdminNotificationDTO();
                notify.setData(date);
                notify.setDescription("C'è una nuova segnalazione da parte dell'utente" + username1 );
                notify.setAdmin(ad);
                notify.setRead(false);

                notifications.add(notify);
            }

            return adminNotificationService.sendNotificationAllAdmin(notifications);
        }
        else
            return false;
    }

    @Override
    public boolean addSupportRequest(String username, SupportDTO supportDTO) {
        SupportDTO supportRequest= new SupportDTO();

        LocalDate date= LocalDate.now();

        supportRequest.setDateRequest(date);
        supportRequest.setType(supportDTO.getType());
        supportRequest.setText(supportDTO.getText());
        supportRequest.setUsername(username);
        supportRequest.setSubject(supportDTO.getSubject());

        if(supportRequestService.addSupportRequest(supportRequest)) {

            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            System.out.println("TOKEN: " + request.getHeader("Authorization"));
            headers.add("Authorization", request.getHeader("Authorization"));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    "http://user-service/user-api/admins",
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            List<String> admins = responseEntity.getBody();


            if(admins==null)
                return false;

            List<AdminNotificationDTO> notifications= new Vector<>();
            AdminNotificationDTO notify;

            for(String ad: admins) {
                notify= new AdminNotificationDTO();
                notify.setData(date);
                notify.setDescription("C'è una nuova richiesta di supporto dall'utente " + username);
                notify.setAdmin(ad);
                notify.setRead(false);

                notifications.add(notify);
            }

            return adminNotificationService.sendNotificationAllAdmin(notifications);
        }
        else
            return false;
    }

    @Override
    @Transactional
    public boolean manageSupportRequest(SupportDTO supportDTO, boolean accept) {
        if(!supportRequestService.deleteSupportRequest(supportDTO))
            return false;

        UserNotificationDTO notification= new UserNotificationDTO();

        LocalDate date= LocalDate.now();
        notification.setData(date);
        notification.setUser(supportDTO.getUsername());
        notification.setDescription();
    }

    @Override
    @Transactional
    public boolean manageReport(String username, ReportDTO reportDTO) {
        if(!reportService.deleteReport(reportDTO))
            return false;

        String descr;
        if(reportDTO.getAdminResponse().isAccept())
            descr= "Segnalazione "+ reportDTO.getReportCode() +" elaborata dall'admin "+username;
        else
            descr= "Segnalazione "+ reportDTO.getReportCode() +" respinta dall'admin "+username;

        return userNotificationService.addUserNotification(username, descr, reportDTO.getAdminResponse().getExplain());
        /*TODO fare controllo per consecutivo ban
          TODO un admin può bannare direttamente e l'utente viene bannato direttamente a quota 5 tuple contenente il suo usernme
          TODO togliere il campo "accept" da DB o metodi (l'admin risponde alle richieste)
        */
    }


}
