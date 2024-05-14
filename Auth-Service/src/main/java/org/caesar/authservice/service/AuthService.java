package org.caesar.authservice.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.authservice.Config.JwtUtil;
import org.caesar.authservice.dto.AuthResponse;
import org.caesar.authservice.dto.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.caesar.authservice.Config.JwtUtil.BASIC_TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(AUTHORIZATION);

        String headerToken = StringUtils.delete(authorizationHeader, BASIC_TOKEN_PREFIX).trim();
        String username = JwtUtil.decodedBase64(headerToken)[0];
        String password = JwtUtil.decodedBase64(headerToken)[1];

        log.info("Username: " + username + " Password: " + password);

        User user = userService.findByEmail(username).orElseThrow(()->new RuntimeException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {

            List<String> roles = Arrays.stream(user.getRoles().split(",")) //
                                                   .map(role -> "ROLE_" + role)// oppure salvare "ROLE_" nel db
                                                   .collect(Collectors.toList());

            return new AuthResponse("Ciao");
        }

        throw new RuntimeException("Bad credential");
    }
}