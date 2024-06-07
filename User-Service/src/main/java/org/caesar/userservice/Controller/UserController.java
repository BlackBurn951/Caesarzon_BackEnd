package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.CardRepository;
import org.caesar.userservice.Data.Services.*;
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
    private final UserAddressService userAddressService;
    private final UserCardService userCardService;


    //End-point per gli utenti
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

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        boolean result= userService.deleteUser();

        if(result)
            return new ResponseEntity<>("User eliminato", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella cancellazione dell'user", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per gli indirizzi
    @GetMapping("/address")
    public ResponseEntity<AddressDTO> getAddressData(@RequestParam("nameLista") String addressName) {
        AddressDTO addressDTO= addressService.getAddress(addressName);

        if(addressDTO!=null)
            return new ResponseEntity<>(addressDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

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

    @GetMapping("/addresses-names")
    public List<String> getAddressesNames() {
        log.debug("Sono nella chiamata get address");
        return userAddressService.getAddresses();
    }

    @DeleteMapping("/address")
    public ResponseEntity<String> deleteAddress(@RequestParam("addr") String addressName) {
        boolean result= addressService.deleteAddress(addressName);

        if(result)
            return new ResponseEntity<>("Indirizzo eliminato", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per le carte
    @GetMapping("/card")
    public ResponseEntity<CardDTO> getCardData(@RequestParam("nameLista") String cardName) {
        CardDTO cardDTO = cardService.getCard(cardName);

        log.debug("Sono nell'end-point del get card");
        if(cardDTO!=null) {
            log.debug("CardDTO not null {}", cardDTO);
            return new ResponseEntity<>(cardDTO, HttpStatus.OK);
        }else {
            log.debug("CardDTO null");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }  //Check fatto

    @PostMapping("/card")
    public ResponseEntity<String> manageUserCardData(@RequestBody CardDTO cardDTO) {
        log.debug("dati in arrivo dal front {}", cardDTO);
        if (cardService.saveCard(cardDTO))
            return new ResponseEntity<>("Carta salvata!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/cards-names")
    public List<String> getCardsNames() {
        return userCardService.getCards();
    }

    @DeleteMapping("/card")
    public ResponseEntity<String> deleteCard(@RequestParam("crd") String cardName) {
        boolean result= cardService.deleteCard(cardName);

        if(result)
            return new ResponseEntity<>("Carta eliminata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /*aggiungere i seguenti end-point:

        5) aggiunta foto profilo(da fare sul nostro db)
     */

}