package org.caesar.notificationservice.GeneralService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.*;
import org.caesar.notificationservice.Dto.*;
import org.caesar.notificationservice.Sagas.ReportOrchestrator;
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
    private final ReportOrchestrator reportOrchestrator;

    private final static String ADMINS_SERVICE= "adminsService";
    private final static String PRODUCT_SERVICE= "productService";

    private boolean fallbackAdmins(Throwable e){
        log.info("Servizio per gli admin non disponibile");
        return false;
    }

    private boolean fallbackProuct(Throwable e){
        log.info("Servizio dei prodotti non disponibile");
        return false;
    }

    @Override
    public boolean addReportRequest(String username1, ReportDTO reportDTO) {
        //Aggiungo al DTO la data e l'username che ha inviato la segnalazione
        reportDTO.setReportDate(LocalDate.now());
        reportDTO.setUsernameUser1(username1);

        //Controllo che l'utente che segnala non abbia già segnalato quella stessa recensione
        if(reportService.findByUsername1AndReviewId(reportDTO.getUsernameUser1(), reportDTO.getReviewId()))
            return false;

        //Aggiungo la segnalazione
        reportDTO.setEffective(true);
        ReportDTO newReportDTO = reportService.addReport(reportDTO);

        //Controllo se il DTO non è nullo e se il numero di segnalazioni ricevute da un utente è minore di 5 (Su diversi prodotti)
        if(newReportDTO != null && reportService.countReportForUser(newReportDTO.getUsernameUser2(), newReportDTO.getReviewId())>=5 && banService.checkIfBanned(newReportDTO.getUsernameUser2())) {
            //passo 1 validare il ban qui V
            //passo 2 validare le notifiche degli admin da cancellare qui V
            //passo 3 validare la cancellazione delle segnalazioni qui V
            //passo 4 validare la cancellazione delle recensioni sul product service
            //passo 5 validare il ban sul user service
            BanDTO banDTO= new BanDTO();

            banDTO.setAdminUsername("System");
            banDTO.setReason("Limite di segnalazioni raggiunto");
            banDTO.setStartDate(LocalDate.now());
            banDTO.setEndDate(null);
            banDTO.setUserUsername(newReportDTO.getUsernameUser2());
            UUID banId= banService.validateBan(banDTO);
            if(banId!=null)
                return reportOrchestrator.processAutomaticBan(banId, newReportDTO);

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

            List<SaveAdminNotificationDTO> notifications= new Vector<>();
            SaveAdminNotificationDTO notify;

            for(String ad: admins) {
                notify= new SaveAdminNotificationDTO();
                notify.setDate(LocalDate.now());
                notify.setSubject("C'è una nuova segnalazione da parte dell'utente: " + username1 );
                notify.setAdmin(ad);
                notify.setReport(newReportDTO);
                notify.setRead(false);

                notifications.add(notify);
            }

            return adminNotificationService.sendNotificationAllAdmin(notifications);
        }
        return false;
    }

    @Override
    @Transactional
    @CircuitBreaker(name= ADMINS_SERVICE, fallbackMethod = "fallbackAdmins")
    public boolean addSupportRequest(String username, SupportDTO supportDTO) {
        supportDTO.setDateRequest(LocalDate.now());
        supportDTO.setUsername(username);
        SupportDTO newSupportDTO = supportRequestService.addSupportRequest(supportDTO);

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

            List<SaveAdminNotificationDTO> notifications= new Vector<>();
            SaveAdminNotificationDTO notify;

            for(String ad: admins) {
                notify= new SaveAdminNotificationDTO();
                notify.setDate(LocalDate.now());
                notify.setSubject("C'è una nuova richiesta di supporto dall'utente " + username);
                notify.setAdmin(ad);
                notify.setRead(false);
                notify.setSupport(newSupportDTO);

                notifications.add(notify);
            }

            return adminNotificationService.sendNotificationAllAdmin(notifications);
        }
        else
            return false;
    }

    @Override
    @Transactional
    public boolean manageSupportRequest(String username, UUID supportId, String explain) {
        SupportResponseDTO supportResponseDTO = new SupportResponseDTO();
        supportResponseDTO.setSupportCode(supportId);
        supportResponseDTO.setExplain(explain);

        SupportDTO supportDTO= supportRequestService.getSupport(supportResponseDTO.getSupportCode());

        boolean delAdminNot = adminNotificationService.deleteBySupport(supportDTO);

        boolean delSupport = supportRequestService.deleteSupportRequest(supportDTO);
        if(supportDTO!=null && delAdminNot && delSupport) {
            String descr= "Richiesta di supporto elaborata dall'admin " + username;

            UserNotificationDTO userNotificationDTO= new UserNotificationDTO();


            userNotificationDTO.setDate(String.valueOf(LocalDate.now()));
            userNotificationDTO.setSubject(descr);
            userNotificationDTO.setExplanation(supportResponseDTO.getExplain());
            userNotificationDTO.setUser(supportDTO.getUsername());
            userNotificationDTO.setRead(false);


            return userNotificationService.addUserNotification(userNotificationDTO);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean manageReport(String username, UUID reviewId, boolean product, boolean accept) {

        ReportDTO reportDTO = reportService.getReportByReviewId(reviewId);

        reportService.validateDeleteReport(reviewId);

        adminNotificationService.validateDeleteByReport(reportDTO);

        if(!product && accept){
            return deleteReview(reviewId);

        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateAdminNotification(List<AdminNotificationDTO> notificationDTO) {

        List<SaveAdminNotificationDTO> saveNotificationDTO = new Vector<>();
        SaveAdminNotificationDTO saveAdminNotificationDTO;

        for(AdminNotificationDTO notify: notificationDTO){
            saveAdminNotificationDTO = new SaveAdminNotificationDTO();

            saveAdminNotificationDTO.setId(notify.getId());
            saveAdminNotificationDTO.setDate(LocalDate.parse(notify.getDate()));
            saveAdminNotificationDTO.setSubject(notify.getSubject());
            saveAdminNotificationDTO.setAdmin(notify.getAdmin());
            saveAdminNotificationDTO.setRead(notify.isRead());

            if(notify.getReportId() == null){
                saveAdminNotificationDTO.setSupport(supportRequestService.getSupport(notify.getSupportId()));
            }else{
                saveAdminNotificationDTO.setReport(reportService.getReport(notify.getReportId()));
            }
            saveNotificationDTO.add(saveAdminNotificationDTO);
        }

        return adminNotificationService.updateAdminNotification(saveNotificationDTO);
    }


    //Metodi di servizio
    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackProduct")
    private boolean deleteReview(UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://product-service/product-api/admin/review?review_id="+reviewId,
                HttpMethod.DELETE,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }
}