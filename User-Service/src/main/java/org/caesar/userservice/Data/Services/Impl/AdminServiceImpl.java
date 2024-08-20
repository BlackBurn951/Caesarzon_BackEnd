package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.KeycloakDAO.AdminRepository;
import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Data.Services.AdminService;
import org.caesar.userservice.Dto.BanDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Vector;

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
    public int validateBan(BanDTO banDTO) {
        return adminRepository.validateBanUser(banDTO.getUserUsername(), true, false);
    }

    //Metodo per sbannare un utente
    @Override
    public int validateSbanUser(String username) {
        return adminRepository.validateBanUser(username, false, false);
    }

    @Override
    public boolean completeBan(String username) {
        return adminRepository.completeBanUser(username);
    }

    @Override
    public boolean rollbackBan(String username, boolean ban) {
        return adminRepository.validateBanUser(username, false, true)==0;
    }


    @Override
    public List<String> getBansUser(int start) {
        List<User> user= adminRepository.findAllBanUsers(start);

        if(user==null || user.isEmpty())
            return new Vector<>();

        return user.stream()
                .map(User::getUsername)
                .toList();
    }

}
