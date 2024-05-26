package org.caesar.userservice.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/user-api")
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

//    @RequestMapping("/users")
//    public String users() {
//        return "Aidi";
//    }

    @RequestMapping("/users")
    public String users() {
        return restTemplate.getForObject("http://product-service/product-api/value", String.class);
    }

}

