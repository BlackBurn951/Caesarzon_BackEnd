package org.caesar.notificationservice.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.BanService;
import org.caesar.notificationservice.Dto.BanDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notify-api")
@RequiredArgsConstructor
@Slf4j
public class BanController {

    private final BanService banService;

    @PostMapping("/ban")
    public ResponseEntity<UUID> validateBanUser() {
            UUID banId= banService.validateBan();

        if(banId!=null)
            return new ResponseEntity<>(banId, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/ban")
    public ResponseEntity<String> confirmBanUser(@RequestBody BanDTO banDTO) {
        if(banService.confirmBan(banDTO))
            return new ResponseEntity<>("Ban eseguito con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'esecuzione del ban...", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @PutMapping("/ban/{banId}")
    public ResponseEntity<String> releaseLock(@PathVariable UUID banId) {
        if(banService.releaseLock(banId))
            return new ResponseEntity<>("Lock rilasciato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'esecuzione del ban...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/ban/{banId}")
    public ResponseEntity<String> rollback(@PathVariable UUID banId) {
        if(banService.rollback(banId))
            return new ResponseEntity<>("Rollback eseguito con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'esecuzione del rollback...", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @PostMapping("/sban/{username}")
    public ResponseEntity<UUID> validateSbanUser(@PathVariable String username) {
        UUID sbanId= banService.validateSbanUser(username);

        if(sbanId!=null)
            return new ResponseEntity<>(sbanId, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/sban/{username}")
    public ResponseEntity<String> confirmSbanUser(@PathVariable String username) {
        if(banService.completeSbanUser(username))
            return new ResponseEntity<>("Sban completato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel completamento dello sban", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
