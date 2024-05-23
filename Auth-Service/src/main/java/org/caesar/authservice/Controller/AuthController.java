package org.caesar.authservice.Controller;

import org.caesar.authservice.Entities.Tokens;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth-api")
public class AuthController {

    @PostMapping("/test")
    public String test(@RequestBody Tokens tokens) {
        System.out.println(tokens.getAccess());
        return tokens.getAccess();
    }
}
