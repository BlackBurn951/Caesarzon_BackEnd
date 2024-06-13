package org.caesar.userservice.Config;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.text.ParseException;

@Component
public class BearerTokenExtractor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String bearerToken = authorizationHeader.substring(7);
            try {
                // Estrarre il nome utente preferito dal token bearer
                String preferredUsername = extractPreferredUsernameFromToken(bearerToken);
                // Impostare il nome utente preferito nella richiesta

                request.setAttribute("preferred_username", preferredUsername);
            } catch (ParseException e) {
                // Gestisci il token non valido
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
        }
        return true;
    }

    private String extractPreferredUsernameFromToken(String bearerToken) throws ParseException {
        // Decodifica il token JWT e ottieni il campo preferred_username
        JwtDecoder jwtDecoder= NimbusJwtDecoder.withJwkSetUri("http://25.24.244.170:8080/realms/CaesarRealm/protocol/openid-connect/certs").build(); // Sostituisci con il tuo issuer URI per JWK
        Jwt jwt = jwtDecoder.decode(bearerToken);

        return jwt.getClaim("preferred_username");
    }
}