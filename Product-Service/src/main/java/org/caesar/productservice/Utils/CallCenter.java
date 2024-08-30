package org.caesar.productservice.Utils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Dto.DeleteReviewDTO;
import org.caesar.productservice.Dto.ReportDTO;
import org.caesar.productservice.Dto.SaveAdminNotificationDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CallCenter {

    private final RestTemplate restTemplate;

    private final String NOTIFICATION_SERVICE= "notifyService";

    private DeleteReviewDTO fallbackValidate(Throwable e) {
        System.out.println("Circuit breaker partito");
        return null;
    }
    private List<SaveAdminNotificationDTO> fallbackCompleteNotify(Throwable e) {
        return null;
    }
    private List<ReportDTO> fallbackCompleteReport(Throwable e) {
        return null;
    }
    private boolean fallbackGeneric(Throwable e) {
        return false;
    }


    //CHIAMATE PER L'ELIMINAZIONE DELLA RECENSIONE
    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackValidate")
    public DeleteReviewDTO validateAndRollbackReportAndNotifications(String username, UUID reviewId, boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<DeleteReviewDTO> response= restTemplate.exchange("http://notification-service/notify-api/user/report?username="+username+"&review-id="+reviewId+"&rollback="+rollback,
                HttpMethod.PUT,
                entity,
                DeleteReviewDTO.class);

        if(response.getStatusCode() == HttpStatus.OK)
            return response.getBody();

        return null;
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean completeNotificationDelete(UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/notifications/"+reviewId,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean completeReportDelete(UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/report/"+reviewId,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean releaseReportLock(List<UUID> reportIds) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<List<UUID>> entity = new HttpEntity<>(reportIds, headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/report",
                HttpMethod.DELETE,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean releaseNotificationLock(List<UUID> notifyIds) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<List<UUID>> entity = new HttpEntity<>(notifyIds, headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/notifications",
                HttpMethod.DELETE,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean rollbackReport(List<ReportDTO> reports) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<List<ReportDTO>> entity = new HttpEntity<>(reports, headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/report",
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean rollbackNotifications(List<SaveAdminNotificationDTO> notifications) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<List<SaveAdminNotificationDTO>> entity = new HttpEntity<>(notifications, headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/notifications",
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }



    //CHIAMATE PER IL PAGAMENTO
    public boolean validatePayment(UUID cardId, double total, boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/balance/payment/"+cardId+"?total="+total+"&rollback="+rollback,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    public boolean completePayment(UUID cardId, double total) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/balance/"+cardId+"?total="+total,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    public boolean releaseLockPayment(UUID cardId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/balance/release/"+cardId,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    public boolean rollbackPayment(UUID cardId, double total) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/balance/"+cardId+"/refund?total="+total,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }



    //CHIAMATE PER CREARE LA NOTIFICA DELL'UTENTE
    public UUID validateNotification() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<UUID> response= restTemplate.exchange(
                "http://notification-service/notify-api/notification",
                HttpMethod.POST,
                entity,
                UUID.class);

        if(response.getStatusCode()==HttpStatus.OK)
            return response.getBody();

        return null;
    }

    public boolean completeNotification(UUID notifyId, String username, String subject, String explanation) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", notifyId.toString());
        requestBody.put("date", String.valueOf(LocalDate.now()));
        requestBody.put("subject", subject);
        requestBody.put("user", username);
        requestBody.put("read", "false");
        requestBody.put("explanation", explanation);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(
                "http://notification-service/notify-api/notification",
                HttpMethod.PUT,
                entity,
                String.class
        ).getStatusCode()== HttpStatus.OK;
    }

    public boolean releaseNotification(UUID notifyId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                "http://notification-service/notify-api/notification/release?notify-id="+notifyId,
                HttpMethod.PUT,
                entity,
                String.class
        ).getStatusCode()== HttpStatus.OK;
    }

    public boolean rollbackNotification(UUID notifyId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                "http://notification-service/notify-api/notification/"+notifyId,
                HttpMethod.DELETE,
                entity,
                String.class
        ).getStatusCode()== HttpStatus.OK;
    }



    //CHIAMATE PER IL RESO
    public boolean validateAndReleasePaymentForReturn(UUID cardId, boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/balance/"+cardId+"?rollback="+rollback,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    public boolean completeOrRollbackPaymentForReturn(UUID cardId, double total, boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/balance?card-id="+cardId+"&total="+total+"&rollback="+rollback,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }
}
