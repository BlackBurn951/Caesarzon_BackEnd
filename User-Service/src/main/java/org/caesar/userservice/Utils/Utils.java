package org.caesar.userservice.Utils;


import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Dto.UserIdDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class Utils {

    private final UserRepository userRepository;

    private final JwtConverter jwtConverter;

    private final ModelMapper modelMapper;

    public UserIdDTO getUserId() {
        String username= jwtConverter.getUsernameFromToken();
        return modelMapper.map(userRepository.findUserByUsername(username), UserIdDTO.class);
    }
}
