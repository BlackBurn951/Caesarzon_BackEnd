package org.caesar.notificationservice.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Dto.ReviewDTO;
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

    public boolean validateReviewDelete(String username) {
        return validateEndpoint(username, false);
    }

    public List<ReviewDTO> completeReviewDelete(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<ReviewDTO>> responseType=
                new ParameterizedTypeReference<>(){};

        return restTemplate.exchange("http://product-service/product-api/admin/review?username="+username,
                HttpMethod.DELETE,
                entity,
                responseType).getBody();
    }

    public boolean rollbackPreCompleteReviewDelete(String username) {
        return validateEndpoint(username, true);
    }

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

    private boolean validateEndpoint(String username, boolean rollback) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://product-service/product-api/admin/review?username="+username+"&rollback="+rollback,
                HttpMethod.PUT,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }



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

    public boolean rollbackBan(String username) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://user-service/user-api/ban/"+username,
                HttpMethod.DELETE,
                entity,
                String.class).getStatusCode() == HttpStatus.OK;
    }
}
