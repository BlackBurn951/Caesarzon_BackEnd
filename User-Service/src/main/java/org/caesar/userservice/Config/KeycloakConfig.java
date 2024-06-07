package org.caesar.userservice.Config;

import org.caesar.userservice.Dto.UserIdDTO;
import org.keycloak.OAuth2Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.keycloak.admin.client.*;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${ADMIN_USER}")
    private String adminUser;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("CaesarRealm")
                .clientId("caesar-app")
                .grantType(OAuth2Constants.PASSWORD)
                .username(adminUser)
                .password(adminPassword)
                .build();
    }

}
