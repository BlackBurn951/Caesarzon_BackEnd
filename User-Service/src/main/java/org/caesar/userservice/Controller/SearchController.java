package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.UserFindDTO;
import org.caesar.userservice.Dto.UserSearchDTO;
import org.caesar.userservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-api")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final GeneralService generalService;
    private final UserService userService;
    private final HttpServletRequest httpServletRequest;



    //End-point per prendere più utenti
    @GetMapping("/users")
    public ResponseEntity<List<UserFindDTO>> getAllUsers(@RequestParam("str") int start) {
        List<UserFindDTO> result= generalService.getUserFind(start);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/users/follower")
    public ResponseEntity<List<UserSearchDTO>> getAllUsearForFollower(@RequestParam("str") int start) {  //start indica l'inizio da dove prendere +20 (non come il pagable)
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<UserSearchDTO> result= generalService.getAllUserForFollower(username, start);

        if (result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/users/{username}")
    public List<String> getUsersByUsernames(@PathVariable String username) {
        return userService.getUsersByUsername(username);
    }
}
