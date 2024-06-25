package org.caesar.gatewayservice.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String ADMIN = "admin";
    public static final String BASIC = "basic";

    private final JwtConverter jwtConverter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.addFilterBefore(corsWebFilter(), SecurityWebFiltersOrder.CORS)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.PUT, "/user-api/user").permitAll()
                        .pathMatchers(HttpMethod.POST, "/user-api/user").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/user-api/user").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-api/user").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-api/users").permitAll()

                        .pathMatchers(HttpMethod.GET, "/user-api/city").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-api/city-data").permitAll()
                        .pathMatchers(HttpMethod.POST, "/user-api/address").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-api/address").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/user-api/address").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-api/addresses-names").permitAll()

                        .pathMatchers(HttpMethod.POST, "/user-api/upload").permitAll()

                        .pathMatchers(HttpMethod.GET, "/user-api/card").permitAll()
                        .pathMatchers(HttpMethod.POST, "/user-api/card").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-api/cards-name").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-api/admins").permitAll()

                        .pathMatchers(HttpMethod.GET, "/search-api/users").permitAll()

                        .pathMatchers(HttpMethod.GET, "/notify-api/report").permitAll()
                        .pathMatchers(HttpMethod.POST, "/notify-api/report").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/report").permitAll()
                        .pathMatchers(HttpMethod.GET, "/notify-api/support").permitAll()
                        .pathMatchers(HttpMethod.POST, "/notify-api/support").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/support").permitAll()

                        .pathMatchers(HttpMethod.POST, "/product-api/product").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/product").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/product-api/product").permitAll()
                        .pathMatchers(HttpMethod.POST, "/product-api/review").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/image").permitAll()
                        .pathMatchers(HttpMethod.POST, "/product-api/image").permitAll()

                        .pathMatchers(HttpMethod.PUT, "/user-api/image").permitAll()

                        .pathMatchers(HttpMethod.GET, "/product-api/review").permitAll()
                        .pathMatchers(HttpMethod.POST, "/product-api/review").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/product-api/review").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/product-api/admin/review").permitAll()

                        .pathMatchers(HttpMethod.GET, "/product-api/search").permitAll()

                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

        return http.build();
    }




    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:4200");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

}