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


    //End-point 2PC per il pagamento
    @PostMapping("/balance/payment/{cardId}")
    public ResponseEntity<String> validatePayment(@PathVariable("cardId") UUID cardId, @RequestParam("total") double total, @RequestParam("rollback") boolean rollback) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.validatePayment(username, cardId, total, rollback);
        if(result)
            return new ResponseEntity<>("Validazione eseguita con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella validazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //End-point completamento
    @PutMapping("/balance/{cardId}")
    public ResponseEntity<String> completePayment(@PathVariable("cardId") UUID cardId, @RequestParam("total") double total) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.completePayment(username, cardId, total);
        if(result)
            return new ResponseEntity<>("Completamento eseguito con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel completamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //End-point rilascio lock
    @PutMapping("/balance/release/{cardId}")
    public ResponseEntity<String> releaseLockPayment(@PathVariable("cardId") UUID cardId) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.releaseLockPayment(username, cardId);
        if(result)
            return new ResponseEntity<>("Completamento eseguito con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel completamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //End-point rollback
    @PostMapping("/balance/{cardId}/refund")
    public ResponseEntity<String> rollbackPayment(@PathVariable("cardId") UUID cardId, @RequestParam("total") double total) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= generalService.rollbackPayment(username, cardId, total);
        if(result)
            return new ResponseEntity<>("Rollback eseguito con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel rollback...", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //End-point 2PC per il reso
    @PostMapping("/balance/{cardId}")
    public ResponseEntity<String> validateAndReleasePaymentForReturn(@PathVariable("cardId") UUID cardId, @RequestParam("rollback") boolean rollback) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.validateAndReleasePaymentForReturn(username, cardId, rollback))
            return new ResponseEntity<>("Validazione eseguita con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella validazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/balance")
    public ResponseEntity<String> completeOrRollbackPaymentForReturn(@RequestParam("card-id") UUID cardId, @RequestParam("total") double total, @RequestParam("rollback") boolean rollback) {
        //Prendendo l'username dell'utente che ha fatto la chiamata
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.completeOrRollbackPaymentForReturn(username, cardId, total,rollback))
            return new ResponseEntity<>("Completamento eseguito con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel completamento...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
