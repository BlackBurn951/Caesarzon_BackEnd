package org.caesar.userservice.Config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessToken;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


//Convertitore per estrarre l'username dal token JWT (di Keycloak)
@Component
@Slf4j
public class JwtConverter {

    public String getUsernameFromToken() {
        try {
            KeycloakAuthenticationToken authentication =
                    (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                AccessToken accessToken = authentication.getAccount().getKeycloakSecurityContext().getToken();
                if (accessToken != null) {
                    log.debug("TOKEEEEEN: " + accessToken.getPreferredUsername());
                    return accessToken.getPreferredUsername();
                }
            }
        } catch (Exception e) {
            // Gestisci l'eccezione in modo appropriato
            e.printStackTrace();
        }
        return null;
    }


}