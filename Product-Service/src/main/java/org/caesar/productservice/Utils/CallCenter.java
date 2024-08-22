package org.caesar.productservice.Utils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Dto.ReportDTO;
import org.caesar.productservice.Dto.SaveAdminNotificationDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CallCenter {

    private final RestTemplate restTemplate;

    private final String NOTIFICATION_SERVICE= "notifyService";

    private int fallbackValidate(Throwable e) {
        return 2;
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



    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackValidate")
    public int validateReportAndNotifications(String username, UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Integer> response= restTemplate.exchange("http://notification-service/notify-api/user/report?username="+username+"&review-id="+reviewId,
                HttpMethod.PUT,
                entity,
                Integer.class);

        if(response.getStatusCode() == HttpStatus.OK)
            return response.getBody().intValue();

        return 2;
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackCompleteNotify")
    public List<SaveAdminNotificationDTO> completeNotificationDelete(UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<SaveAdminNotificationDTO>> responseType=
                new ParameterizedTypeReference<>(){};

        return restTemplate.exchange("http://notification-service/notify-api/user/notifications/"+reviewId,
                HttpMethod.PUT,
                entity,
                responseType).getBody();
    }

    @CircuitBreaker(name= NOTIFICATION_SERVICE, fallbackMethod = "fallbackCompleteReport")
    public List<ReportDTO> completeReportDelete(UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<ReportDTO>> responseType=
                new ParameterizedTypeReference<>(){};

        return restTemplate.exchange("http://notification-service/notify-api/user/report/"+reviewId,
                HttpMethod.PUT,
                entity,
                responseType).getBody();
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
    public boolean rollbackPreComplete(UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/notifications?review-id="+reviewId,
                HttpMethod.PUT,
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
}
