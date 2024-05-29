package org.caesar.userservice.Config;

import org.keycloak.representations.AccessToken;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


//Convertitore per estrarre l'username dal token JWT (di Keycloak)
public class JwtConverter {

    public String getUsernameFromToken() {
        try {
            KeycloakAuthenticationToken authentication =
                    (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                AccessToken accessToken = authentication.getAccount().getKeycloakSecurityContext().getToken();
                if (accessToken != null) {
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
