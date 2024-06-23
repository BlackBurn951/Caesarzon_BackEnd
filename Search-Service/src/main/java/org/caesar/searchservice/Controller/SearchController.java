package org.caesar.searchservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.searchservice.Data.Services.SearchService;
import org.caesar.searchservice.Dto.ProductSearchDTO;
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
    private final SearchService searchService;
    private final HttpServletRequest httpServletRequest;

    @GetMapping("/users")
    public List<String> searchUsers(@RequestParam("username") String query) {
        return restTemplate.getForObject("http://user-service/user-api/users/" + query, List.class);
    }

    @GetMapping("/products")
    public List<ProductSearchDTO> searchProducts(@RequestParam("search") String search) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        return searchService.searchProducts(username, search);
    }
}