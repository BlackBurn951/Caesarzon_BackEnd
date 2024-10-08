package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.AdminService;
import org.caesar.userservice.Data.Services.ProfilePicService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.*;
import org.caesar.userservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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



    //End-point per la gestione delle carte dell'utente
    @GetMapping("/cards/{username}")
    public List<UUID> getCards(@PathVariable String username) {
        return generalService.getUserCards(username);
    }

    @PostMapping("/card/{username}")
    public ResponseEntity<String> saveUserCardData(@PathVariable String username, @RequestBody CardDTO cardDTO) {
        int result=generalService.addCard(username, cardDTO);
        if (result==0)
            return new ResponseEntity<>("Carta salvata!", HttpStatus.OK);
        else if (result==1)
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>("Ragiunto limite massimo di carte!", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //End-point per la gestione degli indirizzi di un utente
    @GetMapping("/addresses/{username}")
    public List<UUID> getAddressesNames(@PathVariable String username) {
        return generalService.getUserAddresses(username);
    }

    @PostMapping("/address/{username}")
    public ResponseEntity<String> saveUserAddressData(@PathVariable String username, @RequestBody AddressDTO addressDTO) {
        int result= generalService.addAddress(username, addressDTO);
        if(result==0)
            return new ResponseEntity<>("Indirizzo salvato!", HttpStatus.OK);
        else if(result==1)
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>("Raggiunto limite massimo di indirizzi!", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //End-point per la gestione dei ban
    @GetMapping("/bans")
    public ResponseEntity<List<UserSearchDTO>> getBan(@RequestParam("str") int start) {
        List<UserSearchDTO> result= generalService.getBans(start);

        if(!result.isEmpty())
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    //End-point per bannare e sbannare un utente utilizzati da un admin
    @PostMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody BanDTO banDTO){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        banDTO.setAdminUsername(username);

        int result= generalService.banUser(banDTO);
        if(result==0)
            return new ResponseEntity<>("Utente bannato con successo", HttpStatus.OK);
        else if(result==1)
            return new ResponseEntity<>("Utente già bannato in precedenza", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel ban dell'utente", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PutMapping("/sban")
    public ResponseEntity<String> sbanUser(@RequestBody SbanDTO sbanDTO) {
        int result= generalService.sbanUser(sbanDTO);
        if(result==0)
            return new ResponseEntity<>("Utente sbannato con successo", HttpStatus.OK);
        else if(result==1)
            return new ResponseEntity<>("Utente già sbannato in precedenza", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nello sban dell'utente", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per bannare e sbannare attraverso saga
    @PostMapping("/ban/{username}")
    public ResponseEntity<String> validateBanUser(@PathVariable String username) {
        int result= adminService.validateBan(username);

        if(result==0)
            return new ResponseEntity<>("Ban validato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel ban dell'user", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/ban")
    public ResponseEntity<String> completeBanUser(@RequestParam("username") String username) {
        if(adminService.completeBanOrSban(username, true))
            return new ResponseEntity<>("Ban completato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel ban dell'user", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/ban/{username}")
    public ResponseEntity<String> releaseLockBan(@PathVariable String username) {
        if(adminService.releaseLock(username))
            return new ResponseEntity<>("Lock rilassato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel rilascio del lock...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/ban")
    public ResponseEntity<String> rollbackBanUser(@RequestParam("username") String username) {
        if(adminService.rollbackBanOrSban(username, false))
            return new ResponseEntity<>("Rollback eseguito!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel rollback...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
