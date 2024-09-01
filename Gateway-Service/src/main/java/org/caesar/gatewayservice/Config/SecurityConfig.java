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

                        //ROTTE PER IL NOTIFICATION-SERVICE

                        //Ban Controller

                        //Rotte per il ban dell'utente
                        .pathMatchers(HttpMethod.POST, "/notify-api/ban").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/ban").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/ban/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/ban/*").hasRole(ADMIN)

                        //Rotte per lo sban dell'utente
                        .pathMatchers(HttpMethod.POST, "/notify-api/sban/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/sban/*").hasRole(ADMIN)

                        //Rotta per prendere i dati dei ban
                        .pathMatchers(HttpMethod.GET, "/notify-api/bans").hasRole(ADMIN)


                        //Notification Controller

                        //Gestione delle notifiche dell'utente
                        .pathMatchers(HttpMethod.GET, "/notify-api/user/notifications").hasRole(BASIC)

                        .pathMatchers(HttpMethod.POST, "/notify-api/notification").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/notification").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/notification/release").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/notification/*").hasAnyRole(BASIC, ADMIN)

                        .pathMatchers(HttpMethod.PUT, "/notify-api/user/notifications").hasRole(BASIC)

                        .pathMatchers(HttpMethod.DELETE, "/notify-api/notification").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/notifications/*").hasRole(BASIC)


                        .pathMatchers(HttpMethod.PUT, "/notify-api/user/notifications/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/user/notifications").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/notify-api/user/notifications").hasRole(BASIC)




                        //Gestione delle notifiche dell'admin
                        .pathMatchers(HttpMethod.GET, "/notify-api/admin/notifications").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/admin/notifications").hasRole(ADMIN)


                        //Report Controller
                        .pathMatchers(HttpMethod.GET, "/notify-api/report").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, "/notify-api/report").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/admin/report").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/user/report").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/notify-api/user/report/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/user/report").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/notify-api/user/report").hasRole(BASIC)


                        //Support Controller
                        .pathMatchers(HttpMethod.GET, "/notify-api/support").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, "/notify-api/support").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/notify-api/support").hasRole(ADMIN)


                        //Delete Controller
                        .pathMatchers(HttpMethod.POST, "/notify-api/user/delete").hasAnyRole(BASIC, ADMIN)



                        //ROTTE PER L'USER-SERVICE

                        //Address Controller
                        .pathMatchers(HttpMethod.GET, "/user-api/city").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.GET, "/user-api/city-data").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.GET, "/user-api/addresses").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/user-api/address").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.POST, "/user-api/address").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/user-api/address").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.GET, "/user-api/user/address/*").hasRole(BASIC)


                        //Card Controller
                        .pathMatchers(HttpMethod.GET, "/user-api/cards").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/user-api/card").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.POST, "/user-api/card").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/user-api/card").hasAnyRole(BASIC, ADMIN)

                        .pathMatchers(HttpMethod.POST, "/user-api/balance/payment/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/user-api/balance/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/user-api/balance/release/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/user-api/balance/*/refund").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/user-api/balance/*").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/user-api/balance").hasAnyRole(BASIC, ADMIN)


                        //Admin Controller
                        .pathMatchers(HttpMethod.GET, "/user-api/admins").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.GET, "/user-api/user/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/user-api/user/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/user-api/image/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/user-api/user/*").hasRole(ADMIN)

                        .pathMatchers(HttpMethod.GET, "/user-api/cards/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, "/user-api/cards/*").hasRole(ADMIN)

                        .pathMatchers(HttpMethod.GET, "/user-api/addresses/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, "/user-api/address/*").hasRole(ADMIN)

                        .pathMatchers(HttpMethod.GET, "/user-api/bans").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, "/user-api/ban").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/user-api/sban").hasRole(ADMIN)

                        .pathMatchers(HttpMethod.POST, "/user-api/ban/*").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/user-api/ban").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/user-api/ban/*").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/user-api/ban").hasAnyRole(BASIC, ADMIN)



                        //Follower Controller
                        .pathMatchers(HttpMethod.GET, "/user-api/followers").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/user-api/follower/*").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.POST, "/user-api/followers").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/user-api/followers/*").hasRole(BASIC)



                        //Search Controller
                        .pathMatchers(HttpMethod.GET, "/user-api/users").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.GET, "/user-api/users/follower").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/user-api/users/*").hasRole(BASIC)



                        //User Controller
                        .pathMatchers(HttpMethod.GET, "/user-api/user").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/user-api/user").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/user-api/user").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/user-api/user").hasRole(BASIC)

                        .pathMatchers(HttpMethod.PUT, "/user-api/logout").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/user-api/otp/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/user-api/password").hasRole(BASIC)

                        .pathMatchers(HttpMethod.GET, "/user-api/image").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.GET, "/user-api/image/*").hasAnyRole(BASIC, ADMIN)

                        .pathMatchers(HttpMethod.PUT, "/user-api/image").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/user-api/image/admin/*").hasRole(ADMIN)



                        //ROTTE PER IL PRODUCT-SERVICE

                        //Order Controller
                        .pathMatchers(HttpMethod.GET, "/product-api/cart").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/product-api/cart").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/product-api/cart/product/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/cart/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/cart").hasRole(BASIC)

                        .pathMatchers(HttpMethod.GET, "/product-api/orders").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/product-api/orders/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.GET, "/product-api/order/products/*/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.GET, "/product-api/order/products/*").hasRole(BASIC)

                        .pathMatchers(HttpMethod.POST, "/product-api/pre-order").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/product-api/rollback/pre-order").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/product-api/purchase").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/product-api/success").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/product-api/refund").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/product-api/refund/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/product-api/orders/notify").hasAnyRole(BASIC, ADMIN)


                        //Product Controller
                        .pathMatchers(HttpMethod.GET, "/product-api/product/*").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/image/*").permitAll()
                        .pathMatchers(HttpMethod.PUT, "/product-api/image/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, "/product-api/product").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/product/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.GET, "/product-api/search").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/new").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/product/offer").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/lastSearchs").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/product-api/lastView").hasRole(BASIC)


                        //Review Controller
                        .pathMatchers(HttpMethod.GET, "/product-api/review/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.GET, "/product-api/review/average").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/reviews").permitAll()
                        .pathMatchers(HttpMethod.GET, "/product-api/reviews/score").permitAll()
                        .pathMatchers(HttpMethod.POST, "/product-api/review").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/review").hasRole(BASIC)
                        .pathMatchers(HttpMethod.PUT, "/product-api/admin/review").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/product-api/admin/review/review-id/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/product-api/admin/reviews").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/product-api/admin/review/*").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/admin/review").hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, "/product-api/admin/review").hasRole(ADMIN)


                        //Wishlist Controller
                        .pathMatchers(HttpMethod.POST, "/product-api/wishlist").hasRole(BASIC)
                        .pathMatchers(HttpMethod.POST, "/product-api/wishlist/product").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/product-api/wishlist/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.GET, "/product-api/wishlist/products").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.GET, "/product-api/wishlists").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.GET, "/product-api/wishlist/all").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.PUT, "/product-api/wishlist/visibility").hasAnyRole(BASIC, ADMIN)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/wishlist/*").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/wishlist/product").hasRole(BASIC)
                        .pathMatchers(HttpMethod.DELETE, "/product-api/wishlist/products").hasRole(BASIC)


                        //Delete Controller
                        .pathMatchers(HttpMethod.POST, "/product-api/user/delete").hasRole(BASIC)

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