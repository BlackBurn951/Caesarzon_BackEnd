package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.AdminService;
import org.caesar.userservice.Data.Services.ProfilePicService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.BanDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserSearchDTO;
import org.caesar.userservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user-api")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final GeneralService generalService;
    private final ProfilePicService profilePicService;
    private final HttpServletRequest httpServletRequest;
//    private final SagaOrchestrator sagaOrchestrator;
//    private final SagaConsumer sagaConsumer;


    @GetMapping("/admins")
    public ResponseEntity<List<String>> getAdmins(){
        List<String> result = adminService.getAdmins();

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //End-point per la gestione degli utenti
    @GetMapping("/user/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username){
        UserDTO result= userService.getUser(username);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<String> addUser(@RequestBody UserDTO userDTO) {
        if(userService.updateUser(userDTO))
            return new ResponseEntity<>("Dati dell'utente modificati con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella modifica dei dati dell'utente...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/image/{username}")
    public ResponseEntity<String> changeImage(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        if(profilePicService.saveImage(username, file, false))
            return new ResponseEntity<>("Immagine caricata con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nel caricamento dell'immagine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        boolean result= generalService.deleteUser(username);

        if(result)
            return new ResponseEntity<>("User eliminato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella cancellazione dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per la gestione dei ban
    @GetMapping("/bans")
    public ResponseEntity<List<UserSearchDTO>> getBan(@RequestParam("str") int start) {
        List<UserSearchDTO> result= generalService.getBans(start);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody BanDTO banDTO){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        banDTO.setAdminUsername(username);
//        sagaOrchestrator.orchestrateSaga(0);
        int result= adminService.banUser(banDTO);
        if(result==0)
            return new ResponseEntity<>("Utente bannato con successo", HttpStatus.OK);
        else if(result==1)
            return new ResponseEntity<>("Utente già bannato in precedenza", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel ban dell'utente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/ban/{username}")
    public ResponseEntity<String> sbanUser(@PathVariable String username) {

        int result= adminService.sbanUser(username);
        if(result==0)
            return new ResponseEntity<>("Utente sbannato con successo", HttpStatus.OK);
        else if(result==1)
            return new ResponseEntity<>("Utente già sbannato in precedenza", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nello sban dell'utente", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
