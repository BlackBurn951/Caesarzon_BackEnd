package org.caesar.userservice.Controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
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
public class UserDataController {

    //Servizi per la comunicazione con la logica di buisiness
    private final UserService userService;
    private final ProfilePicService profilePicService;
    private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;


    //TODO DA PRENDERE LE TUPLE PER CONTROLLARE SE FARE UPDATE O NO

    //End-point per manipolare i dati anagrafici dell'utente
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserData() {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        UserDTO userDTO = userService.getUser(username);

        if(userDTO != null)
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/user")
    public ResponseEntity<String> saveUserData(@RequestBody UserRegistrationDTO userData) {
        if(userService.saveUser(userData))
            return new ResponseEntity<>("User registrato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella registrazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user")
    public ResponseEntity<String> updateUserData(@RequestBody UserDTO userData) {
        if(userService.updateUser(userData))
            return new ResponseEntity<>("User aggiornato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiornamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.deleteUser(username);

        if(result)
            return new ResponseEntity<>("User eliminato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella cancellazione dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //End-point per manipolare la foto profilo
    @GetMapping("/image")
    public ResponseEntity<byte[]> loadImage(){
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        //Presa dell'imagine profilo dell'utente
        byte[] img = profilePicService.getUserImage(username);
        if(img != null)
            return new ResponseEntity<>(img, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();


        if(profilePicService.saveImage(username, file))
            return new ResponseEntity<>("Immagine caricata con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nel caricamento dell'immagine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }





    //End-point per gli utenti
    @GetMapping("/users")
    public ResponseEntity<List<UserSearchDTO>> getUsersSearch(@RequestParam("str") int start) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<UserSearchDTO> result= generalService.getUserSearch(username, start);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
}  //TODO DA CHIEDERE SE SERVE A LUCA




    //Metodo di prova, fatto da ciccio, bifano e cesare <3
    //prendere tutti gli utenti
    @GetMapping("/usersByUsername")
    public List<String> getUsernames(@RequestParam("username") String username) {  //TODO FATTO DA CICCIO
        System.out.printf("oh dio mi hanno chiamato");
        return (userService.getUsersByUsername(username));
    }  //FIXME deve tornare userSerarch (mantanere per luca)


    @GetMapping("/suca")
    public List<User> ajeje() {
        List<User> al= userRepository.findAllUsers(0);
        if(al!=null)
            return al;
        return null;
    }
}