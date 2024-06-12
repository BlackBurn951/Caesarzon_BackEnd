package org.caesar.userservice.Controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    //End-point per gli utenti
//    @GetMapping("/user")
//    public ResponseEntity<UserDTO> getUserData(HttpServletRequest request) {
//        UserDTO userDTO = userService.getUser();
//
//        if(userDTO != null)
//            return new ResponseEntity<>(userDTO, HttpStatus.OK);
//        else
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }

    @GetMapping("/user")
    public Mono<ResponseEntity<UserDTO>> getUserData() {
        return userService.getUser()
                .map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
        if(userService.updateUser(userData))
            return new ResponseEntity<>("User aggiornato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiornamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        boolean result= generalService.deleteUser();

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
        AddressDTO addressDTO= generalService.getUserAddress(addressName);

        if(addressDTO!=null)
            return new ResponseEntity<>(addressDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/address")
    public ResponseEntity<String> saveUserAddressData(@RequestBody AddressDTO addressDTO) {
        if(generalService.addAddress(addressDTO))
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
        return generalService.getUserAddresses();
    }

    @DeleteMapping("/address")
    public ResponseEntity<String> deleteAddress(@RequestParam("addr") String addressName) {
        boolean result= generalService.deleteUserAddress(addressName);

        if(result)
            return new ResponseEntity<>("Indirizzo eliminato correttamente!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per le carte
    @GetMapping("/card")
    public ResponseEntity<CardDTO> getCardData(@RequestParam("nameLista") String cardName) {
        CardDTO cardDTO = generalService.getUserCard(cardName);

        if(cardDTO!=null)
            return new ResponseEntity<>(cardDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/card")
    public ResponseEntity<String> saveUserCardData(@RequestBody CardDTO cardDTO) {
        if (generalService.addCard(cardDTO))
            return new ResponseEntity<>("Carta salvata!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/cards-names")
    public List<String> getCardsNames() {
        return generalService.getUserCards();
    }

    @DeleteMapping("/card")
    public ResponseEntity<String> deleteCard(@RequestParam("crd") String cardName) {
        boolean result= generalService.deleteUserCard(cardName);

        if(result)
            return new ResponseEntity<>("Carta eliminata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //TODO aggiunta carta con la verifica che i numeri di carta non sia presente nel db

}