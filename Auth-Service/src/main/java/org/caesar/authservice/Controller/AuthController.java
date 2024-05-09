package org.caesar.authservice.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth-api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {


    @PostMapping(value = "/login")
    public String login() {
        return "Aidi";
    }

}
