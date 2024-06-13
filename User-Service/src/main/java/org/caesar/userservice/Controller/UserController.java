package org.caesar.userservice.Controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
import org.caesar.userservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/user-api")
@RequiredArgsConstructor
@Slf4j
public class UserController {


    //Servizi per la comunicazione con la logica di buisiness
    private final UserService userService;
    private final CityDataService cityDataService;
//    private final ProfilePicService profilePicService;
    private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;

    //End-point per gli utenti
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserData() {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        UserDTO userDTO = userService.getUser(username);

        if(userDTO != null)
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/user")
    public ResponseEntity<String> postUserData(@RequestBody UserRegistrationDTO userData) {
        if(userService.saveUser(userData))
            return new ResponseEntity<>("User registrato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella registrazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user")
    public ResponseEntity<String> putUserData(@RequestBody UserDTO userData) {
        log.debug("Username dal front {}", userData.getUsername());
        if(userService.updateUser(userData))
            return new ResponseEntity<>("User aggiornato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiornamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.deleteUser(username);

        if(result)
            return new ResponseEntity<>("User eliminato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella cancellazione dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }



//    @PostMapping("/image")
//    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
//        if(profilePicService.saveImage(file)){
//            return new ResponseEntity<>("Immagine caricata con successo!", HttpStatus.OK);
//        }
//        else{
//            return new ResponseEntity<>("Errore nel caricamento dell'immagine", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @GetMapping("/image")
//    public ResponseEntity<byte[]> loadImage(){
//        byte[] img = profilePicService.getImage();
//        if(img != null){
//            log.debug("IMG: "+ img + "IN STRINGA: " + Arrays.toString(img));
//            return new ResponseEntity<>(img, HttpStatus.OK);
//        }
//        else{
//            return new ResponseEntity<>(img, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    //End-point per gli indirizzi
    @GetMapping("/address")
    public ResponseEntity<AddressDTO> getAddressData(@RequestParam("nameLista") String addressName) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        log.debug("Sono nell'end-point");
        AddressDTO addressDTO= generalService.getUserAddress(addressName, username);

        if(addressDTO!=null)
            return new ResponseEntity<>(addressDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/address")
    public ResponseEntity<String> saveUserAddressData(@RequestBody AddressDTO addressDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.addAddress(username, addressDTO))
            return new ResponseEntity<>("Indirizzo salvato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/city")
    public List<String> getSuggerimentoCitta(@RequestParam("sugg") String sugg) {
        return cityDataService.getCities(sugg);
    }

    @GetMapping("/city-data")
    public CityDataSuggestDTO getDatiCitta(@RequestParam("city") String city) {
        return cityDataService.getCityData(city);
    }

    @GetMapping("/addresses-names")
    public List<String> getAddressesNames() {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        return generalService.getUserAddresses(username);
    }

    @DeleteMapping("/address")
    public ResponseEntity<String> deleteAddress(@RequestParam("addr") String addressName) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.deleteUserAddress(username, addressName);

        if(result)
            return new ResponseEntity<>("Indirizzo eliminato correttamente!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per le carte
    @GetMapping("/card")
    public ResponseEntity<CardDTO> getCardData(@RequestParam("nameLista") String cardName) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        CardDTO cardDTO = generalService.getUserCard(username, cardName);

        if(cardDTO!=null)
            return new ResponseEntity<>(cardDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/card")
    public ResponseEntity<String> saveUserCardData(@RequestBody CardDTO cardDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if (generalService.addCard(username, cardDTO))
            return new ResponseEntity<>("Carta salvata!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/cards-names")
    public List<String> getCardsNames() {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        return generalService.getUserCards(username);
    }

    @DeleteMapping("/card")
    public ResponseEntity<String> deleteCard(@RequestParam("crd") String cardName) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.deleteUserCard(username, cardName);

        if(result)
            return new ResponseEntity<>("Carta eliminata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //Metodo di prova, fatto da ciccio, bifano e cesare <3
    //prendere tutti gli utenti
    @GetMapping("/usersByUsername")
    public List<String> getUsernames(@RequestParam("username") String username) {
        System.out.printf("oh dio mi hanno chiamato");
        return (userService.getUsersByUsername(username));
    }
    //TODO aggiunta carta con la verifica che i numeri di carta non sia presente nel db
}
    