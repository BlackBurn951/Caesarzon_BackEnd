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
    public ResponseEntity<UUID> validateBanUser(@RequestBody BanDTO banDTO) {
        UUID banId= banService.validateBan(banDTO);
        if(banId!=null)
            return new ResponseEntity<>(banId, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/ban/{banId}")
    public ResponseEntity<String> confirmBanUser(@PathVariable UUID banId) {
        if(banService.confirmBan(banId))
            return new ResponseEntity<>("Ban eseguito con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'esecuzione del ban...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/ban/{username}")
    public ResponseEntity<String> sbanUser(@PathVariable("username") String username, @RequestParam("confirm") boolean confirm) {
        if(banService.sbanUser(username, confirm))
            return new ResponseEntity<>("Utente sbannato con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nello sban dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
