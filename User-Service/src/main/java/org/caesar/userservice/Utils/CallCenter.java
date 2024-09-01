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
import java.util.*;

@Service
@RequiredArgsConstructor
public class CallCenter {

    private final RestTemplate restTemplate;
    private final static String NOTIFY_SERVICE = "notifyService";
    private final static String PRODUCT_SERVICE = "productService";

    private UUID fallbackValidateBan(Throwable e){
        System.out.println("Servizio per la gestione delle notifiche non disponibile");
        return null;
    }
    private boolean fallbackGenericBan(Throwable e){
        System.out.println("Servizio per la gestione delle notifiche non disponibile");
        return false;
    }
    private boolean fallbackGeneric(Throwable e){
        System.out.println("Circuit breaker per i metodi generici");
        return false;
    }

    //Chiamate per eseguire il ban
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackValidateBan")
    public UUID validateBan() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

         ResponseEntity<UUID> response= restTemplate.exchange("http://notification-service/notify-api/ban",
                HttpMethod.POST,
                entity,
                UUID.class);

         if(response.getStatusCode().value()==200)
             return response.getBody();

         return null;
    }

    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGenericBan")
    public boolean completeBan(UUID banId, BanDTO banDTO) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", banId.toString());
        requestBody.put("reason", banDTO.getReason());
        requestBody.put("startDate", String.valueOf(LocalDate.now()));
        requestBody.put("endDate", null);
        requestBody.put("userUsername", banDTO.getUserUsername());
        requestBody.put("adminUsername", banDTO.getAdminUsername());
        requestBody.put("confirmed", "false");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange("http://notification-service/notify-api/ban",
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }



    //Chiamata per il rollback
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGenericBan")
    public boolean rollback(UUID banId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/ban/"+banId,
                HttpMethod.DELETE,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    //Chiamata per il rilascio del lock
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGenericBan")
    public boolean releaseLock(UUID banId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/ban/"+banId,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }



    //Chiamate per eseguire lo sban
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackValidateBan")
    public UUID validateSban(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UUID> response= restTemplate.exchange("http://notification-service/notify-api/sban/"+username,
                HttpMethod.POST,
                entity,
                UUID.class);

        if(response.getStatusCode()==HttpStatus.OK)
            return response.getBody();

        return null;
    }

    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGenericBan")
    public boolean completeSban(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/sban/"+username,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }



    //SEZIONE ELIMINAZIONE UTENTE

    //End-point notifiche
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean validateNotificationService(boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://notification-service/notify-api/user/delete?rollback="+rollback,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }


    //End-point prodotti
    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean validateProductService(boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://product-service/product-api/user/delete?rollback="+rollback,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }
}
