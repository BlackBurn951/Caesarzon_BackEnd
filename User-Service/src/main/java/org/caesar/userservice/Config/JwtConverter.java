package org.caesar.userservice.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private String username;

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        this.username = jwt.getClaimAsString("preferred_username");
        return null;
    }

    public String getUsernameFromToken(){
        return username;
    }
}