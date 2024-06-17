package org.caesar.authservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.authservice.dto.AuthResponse;
import org.caesar.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth-api")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class AuthController {


    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(HttpServletRequest request, @RequestHeader("X-CSRF-TOKEN") String csrfTokenFromRequest) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");


        if ( csrfToken.getToken() != null && csrfTokenFromRequest != null && csrfToken.getToken().equals(csrfTokenFromRequest)) {
            AuthResponse response = new AuthResponse("Login effettuato con successo");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthResponse("Token CSRF non valido"));
        }
    }
}
