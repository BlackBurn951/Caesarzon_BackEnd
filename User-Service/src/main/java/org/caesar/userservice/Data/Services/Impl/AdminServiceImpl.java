package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.KeycloakDAO.AdminRepository;
import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Data.Services.AdminService;
import org.caesar.userservice.Dto.BanDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final RestTemplate restTemplate;

    private final static String NOTIFY_SERVICE = "notifyService";

    private int fallbackCircuitBreaker(Throwable e){
        log.info("Servizio per la gestione delle notifiche non disponibile");
        return 2;
    }


    //Metodo per restituire tutti gli admins
    @Override
    public List<String> getAdmins() {
        List<Admin> admins = adminRepository.findAllAdmin();
        return admins.stream().map(Admin::getUsername).toList();
    }

    //Metodo per bannare un utente
    @Override
    @Transactional
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = NOTIFY_SERVICE)
    public int banUser(BanDTO banDTO) {
        int result= adminRepository.banUser(banDTO.getUserUsername(), true);
        if(result==0) {
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

            boolean response= restTemplate.exchange("http://notification-service/notify-api/ban",
                    HttpMethod.POST,
                    entity,
                    String.class).getStatusCode() == HttpStatus.OK;
            return response? 0: 2;
        }
        return result;
    }

    //Metodo per sbannare un utente
    @Override
    @Transactional
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = NOTIFY_SERVICE)
    public int sbanUser(String username) {
        int result= adminRepository.banUser(username, false);
        if(result==0) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", request.getHeader("Authorization"));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            boolean response= restTemplate.exchange("http://notification-service/notify-api/ban/" + username,
                    HttpMethod.PUT,
                    entity,
                    String.class).getStatusCode() == HttpStatus.OK;
            return response? 0: 2;
        }
        return result;
    }

    @Override
    public List<String> getBansUser(int start) {
        List<User> user= adminRepository.findAllBanUsers(start);

        if(user==null || user.isEmpty())
            return null;

        return user.stream()
                .map(User::getUsername)
                .toList();
    }

}
