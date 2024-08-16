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
import org.caesar.userservice.Sagas.BanOrchestrator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final RestTemplate restTemplate;
    private final BanOrchestrator banOrchestrator;

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
    public int validateBan(BanDTO banDTO) {
        int result= adminRepository.banUser(banDTO.getUserUsername(), true, false);
        if(result==0) {
            if(banOrchestrator.processBan(banDTO))
                return 0;
            return 1;
        }
        return result;
    }

    //Metodo per sbannare un utente
    @Override
    public int validateSbanUser(String username) {
        int result= adminRepository.banUser(username, false, false);
        if(result==0) {
            if(banOrchestrator.processSban(username))
                return 0;
            return 1;
        }
        return result;
    }

    @Override
    public boolean completeBan(String username) {
        return adminRepository.completeBanUser(username);
    }

    @Override
    public void rollbackBan(String username, boolean ban) {
        adminRepository.banUser(username, ban, true);
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
