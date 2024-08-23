package org.caesar.productservice.Utils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class Utils {

    private final RestTemplate restTemplate;
    private final static String NOTIFY_SERVICE= "notifyService";

    private boolean fallbackCircuitBreaker(Throwable e){
        log.info("Servizio per l'invio delle notifiche non disponibile");
        return false;
    }

    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    public boolean sendNotify(String username, String subject, String explanation){
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("date", String.valueOf(LocalDate.now()));
        requestBody.put("subject", subject);
        requestBody.put("user", username);
        requestBody.put("read", "false");
        requestBody.put("explanation", explanation);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(
                "http://notification-service/notify-api/notification",
                HttpMethod.POST,
                entity,
                String.class
        ).getStatusCode()== HttpStatus.OK;
    }
}
