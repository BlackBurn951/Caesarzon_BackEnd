package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.AddressService;
import org.caesar.userservice.Data.Services.CardService;
import org.caesar.userservice.Data.Services.CityDataService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user-api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    //Servizi per la comunicazione con la logica di buisiness
    private final UserService userService;
    private final AddressService addressService;
    private final CardService cardService;
    private final CityDataService cityDataService;


    //End-point per i dati anagrafici
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserData() {
        return new ResponseEntity<UserDTO>(userService.getUser(), HttpStatus.OK);
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
            return new ResponseEntity<>("User registrato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiornamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per gli indirizzi
    @GetMapping("/address")
    public ResponseEntity<AddressDTO> getAddressData(@RequestParam String addressName) {
        AddressDTO addressDTO= addressService.getAddress(addressName);

        log.debug("Sono nell'end-point del get address");
        if(addressDTO!=null) {
            log.debug("AddressDTO not null {}", addressDTO);
            return new ResponseEntity<>(addressService.getAddress(addressName), HttpStatus.OK);
        }else {
            log.debug("AddressDTO null");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }  //Check eseguito

    @PostMapping("/address")
    public ResponseEntity<String> manageUserAddressData(@RequestBody AddressDTO addressDTO) {
        if(addressService.saveAddress(addressDTO))
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


    //End-point per le carte
    @GetMapping("/card")
    public ResponseEntity<CardDTO> getCardData(@RequestParam String cardName) {
        CardDTO cardDTO = cardService.getCard(cardName);

        log.debug("Sono nell'end-point del get card");
        if(cardDTO!=null) {
            log.debug("CardDTO not null {}", cardDTO);
            return new ResponseEntity<>(cardService.getCard(cardName), HttpStatus.OK);
        }else {
            log.debug("CardDTO null");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }  //Check fatto

    @RequestMapping("/card")
    public ResponseEntity<String> manageUserCardData(@RequestBody CardDTO cardDTO, HttpServletRequest request) {
        boolean isUpdate;
        isUpdate = !request.getMethod().equals("POST");

        log.debug("dati in arrivo dal front {}", cardDTO);
        if (cardService.saveCard(cardDTO))
            return new ResponseEntity<>("Carta salvata!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /*aggiungere i seguenti end-point:

        5) aggiunta foto profilo(da fare sul nostro db)
     */

}










