package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Services.AddressService;
import org.caesar.userservice.Data.Services.CardService;
import org.caesar.userservice.Data.Services.CityDataService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Vector;


@RestController
@RequestMapping("/user-api")
@RequiredArgsConstructor
public class UserController {


    //Servizi per la comunicazione con la logica di buisiness
    private UserService userService;
    private AddressService addressService;
    private CardService cardService;
    private CityDataService cityDataService;

    @RequestMapping("/user")
    public ResponseEntity<String> manageUserData(@RequestBody UserRegistrationDTO userData, HttpServletRequest request) {
        ResponseEntity<String> response;

        if(userService.saveUser(userData)) {
            response= new ResponseEntity<>("User registrato!", HttpStatus.OK);
        } else{
            response= new ResponseEntity<>("Problemi nella registrazione...", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserData() {
        return new ResponseEntity<UserDTO>(userService.getUser(), HttpStatus.OK);
    }


    @RequestMapping("/address")
    public ResponseEntity<String> manageUserAddressData(@RequestBody AddressDTO addressDTO, HttpServletRequest request) {
        ResponseEntity<String> response;

        boolean isUpdate;
        isUpdate = !request.getMethod().equals("POST");

        if(addressService.saveOrUpdateAddress(addressDTO, isUpdate)){
            response= new ResponseEntity<>("Indirizzo salvato!", HttpStatus.OK);
        }else{
            response= new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;

    }

    @GetMapping("/address")
    public ResponseEntity<AddressDTO> getAddressData(@RequestParam String addressName) {
        return new ResponseEntity<AddressDTO>(addressService.getAddress(addressName), HttpStatus.OK);
    }

    @RequestMapping("/card")
    public ResponseEntity<String> manageUserCardData(@RequestBody CardDTO cardDTO, HttpServletRequest request) {
        ResponseEntity<String> response;

        boolean isUpdate;
        isUpdate = !request.getMethod().equals("POST");

        if (cardService.saveOrUpdateCard(cardDTO, isUpdate)) {
            response = new ResponseEntity<>("Carta salvata!", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @GetMapping("/card")
    public ResponseEntity<CardDTO> getCardData(@RequestParam String cardName) {
        return new ResponseEntity<CardDTO>(cardService.getCard(cardName), HttpStatus.OK);
    }


    @PostMapping("/phone-number")
    public ResponseEntity<String> receivePhoneNumber(@RequestBody PhoneNumberDTO phoneNumberDTO) {
        ResponseEntity<String> response;

        if(userService.savePhoneNumber(phoneNumberDTO)){
            response= new ResponseEntity<>("Numero di telefono caricato!", HttpStatus.OK);
        }else{
            response= new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }


    @GetMapping("/city")
    public List<String> getSuggerimentoCitta(@RequestParam("sugg") String sugg) {
        return cityDataService.getCities(sugg);
    }

    @GetMapping("/city-data")
    public CityDataSuggestDTO getDatiCitta(@RequestParam("city") String city) {
        return cityDataService.getCityData(city);
    }


    /*aggiungere i seguenti end-point:

        1) aggiunta numero telefono(da aggiungere come campo su keycloak)
        2) aggiunta indrizzo(da fare sul nostro db)
        3) aggiunta carta(da fare sul nostro db)
        4) DA GUARDARE IMPLEMENTAZIONE PAYPAL
        5) aggiunta foto profilo(da fare sul nostro db)
     */

}










