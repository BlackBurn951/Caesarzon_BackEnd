package org.caesar.authservice.Controller;


import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
public class CsrfTokenController {

    @GetMapping("/get-csrf-token")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return csrfToken.getToken();
    }

}
