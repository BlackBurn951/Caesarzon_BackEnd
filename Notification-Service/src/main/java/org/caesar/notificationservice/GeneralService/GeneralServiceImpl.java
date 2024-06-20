package org.caesar.notificationservice.GeneralService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.*;
import org.caesar.notificationservice.Dto.*;
import org.springframework.http.*;
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
@Slf4j
public class GeneralServiceImpl implements GeneralService{

    private final RestTemplate restTemplate;
    private final ReportService reportService;
    private final SupportRequestService supportRequestService;
    private final AdminNotificationService adminNotificationService;
    private final UserNotificationService userNotificationService;
    private final BanService banService;


    @Override
    @Transactional
    public boolean addReportRequest(String username1, ReportDTO reportDTO) {
        ReportDTO reportRequest= new ReportDTO();

        log.debug("Sono nel metodo dell'add segnalazione");
        reportRequest.setReportDate(LocalDate.now());
        reportRequest.setReason(reportDTO.getReason());
        reportRequest.setDescription(reportDTO.getDescription());
        reportRequest.setUsernameUser1(username1);
        reportRequest.setUsernameUser2(reportDTO.getUsernameUser2());

        log.debug("Prima di aggiungere la tupla della segnalazione");
        if(reportService.addReport(reportRequest)) {
            log.debug("Prima di effettuare la chiamata all'user-api");
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", request.getHeader("Authorization"));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    "http://user-service/user-api/admins",
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            log.debug("Dopo la chiamata dell'user-api");
            List<String> admins = responseEntity.getBody();

            if(admins==null)
                return false;
            log.debug("Dopo controllo della risposta dell'usera-api");
            List<AdminNotificationDTO> notifications= new Vector<>();
            AdminNotificationDTO notify;

            for(String ad: admins) {
                log.debug("Nel for-each");
                notify= new AdminNotificationDTO();
                notify.setData(LocalDate.now());
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
    @Transactional
    public boolean addSupportRequest(String username, SupportDTO supportDTO) {
        SupportDTO supportRequest= new SupportDTO();

        log.debug("Sono nel metodo dell'add richiesta");
        supportRequest.setDateRequest(LocalDate.now());
        supportRequest.setType(supportDTO.getType());
        supportRequest.setText(supportDTO.getText());
        supportRequest.setUsername(username);
        supportRequest.setSubject(supportDTO.getSubject());

        log.debug("Prima di aggiungere la tupla della richiesta");
        if(supportRequestService.addSupportRequest(supportRequest)) {
            log.debug("Prima di effettuare la chiamata all'user-api");
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", request.getHeader("Authorization"));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    "http://user-service/user-api/admins",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            log.debug("Dopo la chiamata dell'user-api");
            List<String> admins = responseEntity.getBody();

            if(admins==null)
                return false;
            log.debug("Dopo controllo della risposta dell'usera-api");
            List<AdminNotificationDTO> notifications= new Vector<>();
            AdminNotificationDTO notify;

            for(String ad: admins) {
                log.debug("Nel for-each");
                notify= new AdminNotificationDTO();
                notify.setData(LocalDate.now());
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
    public boolean manageSupportRequest(String username, SupportResponseDTO sendSupportDTO) {
        log.debug("Prima della presa della richiesta con il codice");
        SupportDTO supportDTO= supportRequestService.getSupport(sendSupportDTO.getSupportCode());

        if(supportDTO!=null && supportRequestService.deleteSupportRequest(supportDTO)) {
            log.debug("Dopo il controllo is null  e la cancellazione della tupla di supporto");
            String descr= "Richiesta di supporto " + supportDTO.getSupportCode() + " elaborata dall'admin " + username;

            NotificationDTO notificationDTO= new NotificationDTO();

            notificationDTO.setDate(LocalDate.now().toString());
            notificationDTO.setSubject(descr);
            notificationDTO.setRead(false);
            notificationDTO.setExplanation(sendSupportDTO.getExplain());

            return userNotificationService.addUserNotification(notificationDTO, supportDTO.getUsername());
        }
        return false;
    }

    @Override
    @Transactional
    public boolean manageReport(ReportResponseDTO reportResponseDTO, String username) {
        log.debug("Subito prima della presa della tupla di segnalazione tramite codice");
        ReportDTO reportDTO= reportService.getReport(reportResponseDTO.getReportCode());

        if(reportDTO!=null && reportService.deleteReport(reportDTO)) {
            log.debug("Subito dopo il controllo is null e la cancellazione della tupla");
            if(!reportResponseDTO.isAccept())
                return true;

            log.debug("Dopo il controllo che la segnalazione sia accettata o meno");
            BanDTO banDTO= new BanDTO();
            banDTO.setReason(reportResponseDTO.getExplain());
            banDTO.setStartDate(LocalDate.now());
            banDTO.setUserUsername(reportDTO.getUsernameUser2());
            banDTO.setAdminUsername(username);

            if(banService.banUser(banDTO)) {
                log.debug("Dopo l'aggiunta della tupla del ban e prima della chiamata all'user-api");
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", request.getHeader("Authorization"));

                HttpEntity<String> entity = new HttpEntity<>(headers);

                return restTemplate.exchange("http://user-service/user-api/ban/" + reportDTO.getUsernameUser2(),
                        HttpMethod.POST,
                        entity,
                        String.class).getStatusCode() == HttpStatus.OK;
            }
        }
        return false;
    }
}