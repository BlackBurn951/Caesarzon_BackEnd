package org.caesar.userservice.Controller;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.GeneralService.GeneralService;
import org.caesar.userservice.GeneralService.GeneralServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user-api")
public class UserController {

    //Servizio per la comunicazione con la logica di buisiness
    private final GeneralService userService;

    @PostMapping("/user")
    public ResponseEntity<String> receiveData(@RequestBody UserDTO userData) {
        ResponseEntity<String> response;
        if(userService.saveUser(userData)) {
            response= new ResponseEntity<>("User registrato!", HttpStatus.OK);
            return response;
        } else{
            response= new ResponseEntity<>("Problemi nella registrazione...", HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        }
    }



}










