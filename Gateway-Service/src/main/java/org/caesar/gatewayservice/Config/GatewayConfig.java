package org.caesar.gatewayservice.Config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder){

        return builder
                .routes()
                .route("auth-service", r -> r.path("/auth-api/**")
                        .uri("lb://auth-service"))
                                                                                                                            .build();

    }

}
