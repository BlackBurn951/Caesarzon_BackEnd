package org.caesar.userservice.Data.Services.Impl;
import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.KeycloakDAO.AdminRepository;
import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Services.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public List<String> getAdmins() {
        List<Admin> admins = adminRepository.findAllAdmin();
        for(Admin admin : admins) {
            System.out.println("Admin: " + admin);
        }
        return admins.stream().map(Admin::getUsername).toList();
    }
}
