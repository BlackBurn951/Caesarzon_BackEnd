package org.caesar.authservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.authservice.dto.AuthResponse;
import org.caesar.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth-api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public String login(HttpServletRequest request) {
        System.out.println("Ciao test");
        return "Ciao";
    }

}
