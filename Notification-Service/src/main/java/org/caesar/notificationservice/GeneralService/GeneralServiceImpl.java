package org.caesar.notificationservice.GeneralService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.AdminNotificationRepository;
import org.caesar.notificationservice.Data.Dao.ReportRepository;
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
import java.util.UUID;
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
    private final AdminNotificationRepository adminNotificationRepository;
    private final ReportRepository reportRepository;


    @Override
    @Transactional
    public boolean addReportRequest(String username1, ReportDTO reportDTO) {
        try {
            //Aggiungo al DTO la data e l'username che ha inviato la segnalazione
            reportDTO.setReportDate(LocalDate.now());
            reportDTO.setUsernameUser1(username1);

            //Aggiungo la segnalazione
            ReportDTO newReportDTO = reportService.addReport(reportDTO);

            //Controllo se il DTO non è nullo e se il numero di segnalazioni ricevute da un utente è minore di 5 (Su diversi prodotti)
            if(newReportDTO != null && reportService.countReportForUser(newReportDTO.getUsernameUser2(), newReportDTO.getReviewId())>=5) {
                BanDTO banDTO= new BanDTO();

                banDTO.setAdminUsername("System");
                banDTO.setReason("Limite di segnalazioni raggiunto");
                banDTO.setStartDate(LocalDate.now());
                banDTO.setEndDate(null);
                banDTO.setUserUsername(newReportDTO.getUsernameUser2());

                //Eliminazione delle notifiche relative alla segnalazione per tutti gli admin
                adminNotificationRepository.deleteByReportId(reportDTO.getReviewId());

                //Eliminazione della segnalazione
                reportRepository.deleteById(reportDTO.getId());

                return banService.banUser(banDTO) && deleteReview(reportDTO.getReviewId());
            } else if(newReportDTO != null) {
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

                List<String> admins = responseEntity.getBody();

                if(admins==null)
                    return false;

                List<AdminNotificationDTO> notifications= new Vector<>();
                AdminNotificationDTO notify;

                for(String ad: admins) {
                    notify= new AdminNotificationDTO();
                    notify.setDate(LocalDate.now());
                    notify.setDescription("C'è una nuova segnalazione da parte dell'utente" + username1 );
                    notify.setAdmin(ad);
                    notify.setReportId(newReportDTO.getId());
                    notify.setRead(false);

                    notifications.add(notify);
                }
                return adminNotificationService.sendNotificationAllAdmin(notifications);
            }
            return false;
        } catch (Exception | Error e) {
            log.debug("Errore nella gestione della richiesta");
            return false;
        }
    }

    @Override
    @Transactional //TODO COSI FUNZIONA
    public boolean addSupportRequest(String username, SupportDTO supportDTO) {

        supportDTO.setDateRequest(LocalDate.now());
        supportDTO.setUsername(username);
        System.out.println("Sono dentro");
        SupportDTO newSupportDTO = supportRequestService.addSupportRequest(supportDTO);
        System.out.println("Ho salvato la richiesta");

        if(newSupportDTO != null) {
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
            List<String> admins = responseEntity.getBody();

            if(admins==null)
                return false;

            List<AdminNotificationDTO> notifications= new Vector<>();
            AdminNotificationDTO notify;

            for(String ad: admins) {
                notify= new AdminNotificationDTO();
                notify.setDate(LocalDate.now());
                notify.setDescription("C'è una nuova richiesta di supporto dall'utente " + username);
                notify.setAdmin(ad);
                notify.setReportId(null);
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
        SupportDTO supportDTO= supportRequestService.getSupport(sendSupportDTO.getSupportCode());

        if(supportDTO!=null && supportRequestService.deleteSupportRequest(supportDTO)) {
            String descr= "Richiesta di supporto " + supportDTO.getId()+ " elaborata dall'admin " + username;

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
    public boolean manageReport(String username, UUID reviewId, boolean product, boolean accept) {

        reportService.deleteReport(reviewId);
        adminNotificationRepository.deleteByReportId(reviewId);

        if(!product && accept){
            return deleteReview(reviewId);

        }
        return false;
    }


    boolean deleteReview(UUID reviewId){
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://product-service/product-api/admin/review/?review_id="+reviewId,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }
}