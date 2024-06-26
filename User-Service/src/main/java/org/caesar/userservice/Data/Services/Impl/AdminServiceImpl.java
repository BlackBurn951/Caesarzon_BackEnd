package org.caesar.userservice.Data.Services.Impl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.KeycloakDAO.AdminRepository;
import org.caesar.userservice.Data.Entities.Admin;
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

    //Metodo per restituire tutti gli admins
    @Override
    public List<String> getAdmins() {
        List<Admin> admins = adminRepository.findAllAdmin();
        return admins.stream().map(Admin::getUsername).toList();
    }

    //Metodo per bannare un utente
    @Override
    public boolean banUser(BanDTO banDTO) {
        try {
            if(adminRepository.banUser(banDTO.getUserUsername(), true)) {
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


                return restTemplate.exchange("http://notification-service/notify-api/ban",
                        HttpMethod.POST,
                        entity,
                        String.class).getStatusCode() == HttpStatus.OK;
            }
            return false;
        } catch(Exception | Error e) {
            log.debug("Errore nel ban dell'utente");
            return false;
        }
    }

    //Metodo per sbannare un utente
    @Override
    public boolean sbanUser(String username) {
        try {
            if(adminRepository.banUser(username, false)) {
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", request.getHeader("Authorization"));

                HttpEntity<String> entity = new HttpEntity<>(headers);

                return restTemplate.exchange("http://notification-service/notify-api/ban/" + username,
                        HttpMethod.PUT,
                        entity,
                        String.class).getStatusCode() == HttpStatus.OK;
            }
            return false;
        } catch(Exception | Error e) {
            log.debug("Errore nel ban dell'utente");
            return false;
        }
    }



}
