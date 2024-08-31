package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Services.CityDataService;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CityDataSuggestDTO;
import org.caesar.userservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-api")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final GeneralService generalService;
    private final CityDataService cityDataService;
    private final HttpServletRequest httpServletRequest;


    //End-point per l'auto-completamento dei campi indirizzo
    @GetMapping("/city")
    public List<String> getSuggerimentoCitta(@RequestParam("sugg") String sugg) {
        return cityDataService.getCities(sugg);
    }

    @GetMapping("/city-data")
    public CityDataSuggestDTO getDatiCitta(@RequestParam("city") String city) {
        return cityDataService.getCityData(city);
    }


    //End-point per la gestione degli indirizzi
    @GetMapping("/addresses")
    public List<UUID> getAddressesNames() {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        return generalService.getUserAddresses(username);
    }

    @GetMapping("/address")
    public ResponseEntity<AddressDTO> getAddressData(@RequestParam("address_id") UUID id) {
        AddressDTO addressDTO = generalService.getUserAddress(id);
        System.out.println(addressDTO.getId()+"\n"+addressDTO.getRoadName()+"\n"+addressDTO.getRoadType()+"\n"+addressDTO.getCity().getId());
        if(addressDTO!=null)
            return new ResponseEntity<>(addressDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/address")
    public ResponseEntity<String> saveUserAddressData(@RequestBody AddressDTO addressDTO) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        System.out.println("indirizzo arrivato: "+addressDTO.toString());
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        System.out.println("Dati arrivati: "+addressDTO.getRoadType()+"\n"+addressDTO.getRoadName()+"\n"+addressDTO.getHouseNumber()+"\n"+addressDTO.getCity().getId());
        int result= generalService.addAddress(username, addressDTO);
        if(result==0)
            return new ResponseEntity<>("Indirizzo salvato!", HttpStatus.OK);
        else if(result==1)
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>("Raggiunto limite massimo di indirizzi!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/address")
    public ResponseEntity<String> deleteAddress(@RequestParam("address_id") UUID id) {
        boolean result= generalService.deleteUserAddress(id);

        if(result)
            return new ResponseEntity<>("Indirizzo eliminato correttamente!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per controllare se l'indirizzo di spedizione è esistent (chiamato dal microservizio dei prodotti)
    @GetMapping("/user/address/{addressId}")
    public ResponseEntity<Boolean> getUserAddress(@PathVariable UUID addressId) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        System.out.println(username);
        boolean result= generalService.checkAddress(username, addressId);
        System.out.println(result);
        if(result)
            return new ResponseEntity<>(true, HttpStatus.OK);
        else
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
