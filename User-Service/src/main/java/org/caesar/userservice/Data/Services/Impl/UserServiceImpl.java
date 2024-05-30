package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public boolean saveUser(UserDTO userData) {
        return userRepository.saveUser(userData);
    }

    @Override
    public boolean savePhoneNumber(PhoneNumberDTO phoneNumberDTO) {
        return userRepository.savePhoneNumber(phoneNumberDTO);
    }
}
