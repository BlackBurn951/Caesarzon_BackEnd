package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.FollowerService;
import org.caesar.userservice.Data.Services.UserService;
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
public class FollowerController {

    private final GeneralService generalService;
    private final FollowerService followerService;
    private final UserService userService;
    private final HttpServletRequest httpServletRequest;


    @GetMapping("/followers")
    public ResponseEntity<List<UserSearchDTO>> getFollowers(@RequestParam("flw") int flw, @RequestParam("friend") boolean friend) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        return new ResponseEntity<>(generalService.getFollowersOrFriend(username, flw, friend), HttpStatus.OK);
    }

    @GetMapping("/follower/{friendUsername}")
    public boolean isFriend(@PathVariable String friendUsername, @RequestParam("username") String username) {
        return followerService.isFriend(username, friendUsername);
    }

    @PostMapping("/followers")
    public ResponseEntity<String> addUpdateFollower(@RequestBody List<UserSearchDTO> followers) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(!userService.userIsOnChanges(username) && followerService.addFollowers(username, followers))
            return new ResponseEntity<>("Followers registrati!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella registrazione dei followers...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/followers")
    public ResponseEntity<String> deleteFollower(@RequestBody List<String> followers) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(followerService.deleteFollowers(username, followers))
            return new ResponseEntity<>("Followers eliminati con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione dei followers...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
