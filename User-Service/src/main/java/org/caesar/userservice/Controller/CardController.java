package org.caesar.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Dto.CardDTO;
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
public class CardController {

    private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;


    //End-point per la gestione delle carte
    @GetMapping("/cards")
    public List<UUID> getCards() {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        return generalService.getUserCards(username);
    }

    @GetMapping("/card")
    public ResponseEntity<CardDTO> getCardData(@RequestParam("card_id") UUID id) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        CardDTO cardDTO = generalService.getUserCard(id);

        if(cardDTO!=null)
            return new ResponseEntity<>(cardDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/card")
    public ResponseEntity<String> saveUserCardData(@RequestBody CardDTO cardDTO) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        int result=generalService.addCard(username, cardDTO);
        if (result==0)
            return new ResponseEntity<>("Carta salvata!", HttpStatus.OK);
        else if (result==1)
            return new ResponseEntity<>("Problemi nell'inserimento...", HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>("Ragiunto limite massimo di carte!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/card")
    public ResponseEntity<String> deleteCard(@RequestParam("card_id") UUID id) {
        boolean result= generalService.deleteUserCard(id);
        if(result)
            return new ResponseEntity<>("Carta eliminata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point chiamato dal microservizio dei prodotti per pagare in caso di acquisto con la carta
    @PostMapping("/balance/{cardId}")
    public ResponseEntity<Boolean> pay(@PathVariable("cardId") UUID cardId, @RequestParam("total") double total, @RequestParam("refund") boolean refund) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.pay(username, cardId, total, refund);
        if(result)
            return new ResponseEntity<>(true, HttpStatus.OK);
        else
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
