//package org.caesar.authservice.Config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//@Configuration
//@EnableWebSecurity
//public class WebFilter implements org.springframework.web.server.WebFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        String key= CsrfToken.class.getName();
//        Mono<CsrfToken> csrfToken =null!=exchange.getAttribute(key) ?
//                exchange.getAttribute(key):Mono.empty();
//        assert csrfToken != null;
//        return csrfToken.doOnSuccess(token->{
//            ResponseCookie cookie=ResponseCookie.from("XSRF-TOKEN", token.getToken()).maxAge(Duration.ofHours(1))
//                    .httpOnly(false).path("/").build();
//            System.out.println("Cookie {} : "+cookie);
//            exchange.getResponse().getCookies().add("XSRF-TOKEN", cookie);
//        }).then(chain.filter(exchange));
//
//    }
//}
