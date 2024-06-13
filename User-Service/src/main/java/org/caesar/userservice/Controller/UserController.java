package org.caesar.userservice.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
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
    private final ProfilePicService profilePicService;

    //End-point per gli utenti
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserData() {
        UserDTO userDTO = userService.getUser();

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
        if(userService.updateUser(userData))
            return new ResponseEntity<>("User aggiornato!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiornamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user") //FIXME aggiustare dipendenza circolare e gestione errori
    public ResponseEntity<String> deleteUser() {
        boolean result= userService.deleteUser();

        if(result)
            return new ResponseEntity<>("User eliminato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella cancellazione dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /*@GetMapping("/image")
    public ResponseEntity<byte[]> uploadImage(){
        byte[] image= profilePicService.getImage();

        if(image!=null){
            return new ResponseEntity<>(image, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/base-image")
    public ResponseEntity<byte[]> getBaseImage() {
        byte[] baseImageBytes = profilePicService.getBaseImageBytes(); // Metodo per ottenere l'immagine di base come array di byte dal servizio
        if (baseImageBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Imposta il tipo di contenuto dell'header come immagine JPEG
            return new ResponseEntity<>(baseImageBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        if(profilePicService.saveImage(file)){
            return new ResponseEntity<>("Immagine caricata con successo!", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Errore nel caricamento dell'immagine", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

        log.debug("Sono nella chiamata dell'eliminazione dell'indirizzo");
        if(result) {
            log.debug("Sono prima della risposta affermativa");
            return new ResponseEntity<>("Indirizzo eliminato correttamente!", HttpStatus.OK);
        }
        else {
            log.debug("Sono prima della risposta negativa");
            return new ResponseEntity<>("Problemi nell'eliminazione", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    //Metodo di prova, fatto da ciccio, bifano e cesare <3
    //prendere tutti gli utenti
    @GetMapping("/usersByUsername")
    public List<String> getUsernames(@RequestParam("username") String username) {
        System.out.printf("oh dio mi hanno chiamato");
        return (userService.getUsersByUsername(username));
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
    @GetMapping("/suca")
    public Mono<List<String>> suca() {
        List<String> suca = new ArrayList<>();
        suca.add("Mammata");
        suca.add("Dio cane");
        suca.add("Coglione");
        return Mono.just(suca);
    }
}