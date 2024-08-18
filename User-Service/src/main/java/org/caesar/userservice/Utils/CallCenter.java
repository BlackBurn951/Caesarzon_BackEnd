package org.caesar.userservice.Utils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Dto.BanDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CallCenter {

    private final RestTemplate restTemplate;
    private final static String NOTIFY_SERVICE = "notifyService";

    private UUID fallbackValidateBan(Throwable e){
        System.out.println("Servizio per la gestione delle notifiche non disponibile");
        return null;
    }

    private boolean fallbackGenericBan(Throwable e){
        System.out.println("Servizio per la gestione delle notifiche non disponibile");
        return false;
    }

    //Chiamate per eseguire il ban
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackValidateBan")
    public UUID validateBan(BanDTO banDTO) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("reason", banDTO.getReason());
        requestBody.put("startDate", String.valueOf(LocalDate.now()));
        requestBody.put("endDate", null);
        requestBody.put("userUsername", banDTO.getUserUsername());
        requestBody.put("adminUsername", banDTO.getAdminUsername());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

         ResponseEntity<UUID> response= restTemplate.exchange("http://notification-service/notify-api/ban",
                HttpMethod.POST,
                entity,
                UUID.class);

         if(response.getStatusCode().value()==200)
             return response.getBody();

         return null;
    }

    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGenericBan")
    public boolean completeBan(UUID banId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/ban/"+banId,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }


    //Chiamate per eseguire lo sban
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGenericBan")
    public boolean validateSban(String username) {
        return sbanCall(username, "false");
    }

    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGenericBan")
    public boolean completeSban(String username) {
        return sbanCall(username, "true");
    }


    private boolean sbanCall(String username, String confirm) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/ban/" + username+"?confirm="+confirm,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }
}
