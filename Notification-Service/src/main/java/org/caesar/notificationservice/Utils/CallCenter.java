package org.caesar.notificationservice.Utils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Dto.ReviewDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CallCenter {

    private final RestTemplate restTemplate;
    private final String ADMIN_SERVICE= "adminsService";
    private final String PRODUCT_SERVICE= "productService";

    private boolean fallbackGeneric(Throwable e) {
        System.out.println("Circuit breaker nel call center attivato!");
        return false;
    }

    private List<ReviewDTO> fallbackReviewsDelete(Throwable e) {
        System.out.println("Circuit breaker nel call center attivato!");
        return null;
    }

    private ReviewDTO fallbackReviewDelete(Throwable e) {
        System.out.println("Circuit breaker nel call center attivato!");
        return null;
    }


    //End-point per l'eliminazione di tutte le recensioni di un utente
    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackReviewsDelete")
    public List<ReviewDTO> validateAndRollbackReviewDeleteByUsername(String username, boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        ParameterizedTypeReference<List<ReviewDTO>> responseType=
                new ParameterizedTypeReference<>(){};

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<ReviewDTO>> response= restTemplate.exchange("http://product-service/product-api/admin/reviews?username="+username+"&rollback="+rollback,
                HttpMethod.PUT,
                entity,
                responseType);

        if(response.getStatusCode()==HttpStatus.OK)
            return response.getBody();

        return null;
    }

    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean completeReviewDeleteByUsername(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://product-service/product-api/admin/review/"+username,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean releaseReviewLock(List<UUID> reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<List<UUID>> entity = new HttpEntity<>(reviewId, headers);

        return restTemplate.exchange("http://product-service/product-api/admin/review",
                HttpMethod.DELETE,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }

    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean rollbackReviewDelete(List<ReviewDTO> reviews) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        HttpEntity<List<ReviewDTO>> entity = new HttpEntity<>(reviews, headers);

        return restTemplate.exchange("http://product-service/product-api/admin/review",
                HttpMethod.POST,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }


    //End-point per l'eliminazione della singola recensione   //TODO DA AGGIUSTARE CIRCUIT BREAKER
    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackReviewDelete")
    public ReviewDTO validateReviewDeleteById(UUID reviewId, boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ReviewDTO> response= restTemplate.exchange("http://product-service/product-api/admin/review?review-id="+reviewId+"&rollback="+rollback,
                HttpMethod.PUT,
                entity,
                ReviewDTO.class);

        if(response.getStatusCode()==HttpStatus.OK)
            return response.getBody();

        return null;
    }

    @CircuitBreaker(name= PRODUCT_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean completeReviewDeleteById(UUID reviewId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://product-service/product-api/admin/review/review-id/"+reviewId,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode()==HttpStatus.OK;
    }



    //End-point per il ban dell'utente
    @CircuitBreaker(name= ADMIN_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean validateBan(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/ban/"+username,
                HttpMethod.POST,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }

    @CircuitBreaker(name= ADMIN_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean completeBan(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/ban?username="+username,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }

    @CircuitBreaker(name= ADMIN_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean releaseBanLock(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/ban/"+username,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }

    @CircuitBreaker(name= ADMIN_SERVICE, fallbackMethod = "fallbackGeneric")
    public boolean rollbackBan(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/ban?username="+username,
                HttpMethod.DELETE,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }
}
