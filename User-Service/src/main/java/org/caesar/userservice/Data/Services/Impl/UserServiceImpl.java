package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserIdDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final JwtConverter jwtConverter;

    private final ModelMapper modelMapper;

    @Override
    public UserDTO getUser() {
        String username= jwtConverter.getUsernameFromToken();
        return modelMapper.map(userRepository.findUserByUsername(username), UserDTO.class);
    }

    @Override
    public UserIdDTO getUserId() {
        String username= jwtConverter.getUsernameFromToken();
        return modelMapper.map(userRepository.findUserByUsername(username), UserIdDTO.class);
    }

    @Override
    public boolean saveUser(UserRegistrationDTO userRegistrationDTO) {
        return userRepository.saveUser(userRegistrationDTO);
    }

    @Override
    public boolean savePhoneNumber(PhoneNumberDTO phoneNumberDTO) {
        return userRepository.savePhoneNumber(phoneNumberDTO);
    }
}
