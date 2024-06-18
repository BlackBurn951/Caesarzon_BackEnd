package org.caesar.searchservice.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/search-api")
@Slf4j
@RequiredArgsConstructor
public class SearchController {

    private final RestTemplate restTemplate;

    @GetMapping("/search/users")
    public List<String> searchUsers(@RequestParam("username") String query) {
        System.out.println("Provo a fare la chiamata");
        return restTemplate.getForObject("http://user-service/user-api/usersByUsername?username=" + query, List.class);
    }

    /*
    @GetMapping("/search/users")
    public Mono<String> getString() {
        return webClientBuilder
                .build()
                .get()
                .uri("http://user-service/user-api/hello")
                .retrieve()
                .bodyToMono(String.class);
    }/*
    @GetMapping("/search/users")
    public Mono<List<String>> searchUsers() {
        System.out.println("Provo a fare la chiamata");
        return webClientBuilder
                .build()
                .get()
                .uri("http://user-service/user-api/suca")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }*/


}