package org.caesar.userservice.Config;

import org.keycloak.representations.AccessToken;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtConverter {

    public String getUsernameFromToken() {
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        AccessToken accessToken = authentication.getAccount().getKeycloakSecurityContext().getToken();
        return accessToken.getPreferredUsername();
    }

}
