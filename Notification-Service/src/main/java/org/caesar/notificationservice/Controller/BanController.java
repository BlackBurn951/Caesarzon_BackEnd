package org.caesar.notificationservice.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.BanService;
import org.caesar.notificationservice.Dto.BanDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify-api")
@RequiredArgsConstructor
@Slf4j
public class BanController {

    private final BanService banService;

    @PostMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody BanDTO banDTO) {
        if(banService.banUser(banDTO))
            return new ResponseEntity<>("Utente sbannato con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nello sban dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/ban/{username}")
    public ResponseEntity<String> sbanUser(@PathVariable("username") String username) {
        if(banService.sbanUser(username))
            return new ResponseEntity<>("Utente sbannato con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nello sban dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
