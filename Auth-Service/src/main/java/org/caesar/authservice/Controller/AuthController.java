package org.caesar.authservice.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth-api")
public class AuthController {

    @GetMapping("/test")
    public String test() {
        return "testRiuscito!!!";
    }
}
