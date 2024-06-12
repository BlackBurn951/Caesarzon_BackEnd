//package org.caesar.userservice.Config;
//
//import org.keycloak.representations.AccessToken;
//import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//
////Convertitore per estrarre l'username dal token JWT (di Keycloak)
//@Component
//public class JwtConverter {
//
//    public String getUsernameFromToken() {
//        try {
//            KeycloakAuthenticationToken authentication =
//                    (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//
//            if (authentication != null) {
//                System.out.println("Autentication presente");
//                AccessToken accessToken = authentication.getAccount().getKeycloakSecurityContext().getToken();
//                if (accessToken != null) {
//                    return accessToken.getPreferredUsername();
//                }
//            }
//        } catch (Exception e) {
//            // Gestisci l'eccezione in modo appropriato
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//}

package org.caesar.userservice.Config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessToken;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class JwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtConverterProperties properties;

    public JwtConverter(JwtConverterProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        AbstractAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
        return Mono.just(token);
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (properties.getPrincipalAttribute() != null) {
            claimName = properties.getPrincipalAttribute();
        }

        String username = jwt.getClaimAsString("preferred_username"); // Access preferred_username claim
        System.out.println("Username preso dal token " + username);
        return jwt.getClaim(claimName);
    }


    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
                || (resourceRoles = (Collection<String>) resource.get("roles")) == null) {

            return Set.of();
        }

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    public String getUsernameFromToken() {
        try {
            KeycloakAuthenticationToken authentication =
                    (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                System.out.println("Autentication presente");
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