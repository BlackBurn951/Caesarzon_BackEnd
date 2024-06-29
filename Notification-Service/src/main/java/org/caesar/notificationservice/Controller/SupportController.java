package org.caesar.notificationservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.SupportRequestService;
import org.caesar.notificationservice.Dto.SupportDTO;
import org.caesar.notificationservice.Dto.SupportResponseDTO;
import org.caesar.notificationservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notify-api")
@RequiredArgsConstructor
@Slf4j
public class SupportController {

    private final HttpServletRequest httpServletRequest;
    private final GeneralService generalService;
    private final SupportRequestService supportRequestService;

    @GetMapping("/support")
    public ResponseEntity<List<SupportDTO>> getSupports(@RequestParam("num") int num) {
        List<SupportDTO> result = supportRequestService.getAllSupportRequest(num);
        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/support")
    public ResponseEntity<String> sendReport(@RequestBody SupportDTO supportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.addSupportRequest(username , supportDTO))
            return new ResponseEntity<>("Richiesta di supporto inviata con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'invio della richiesta di supporto...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/support")
    public ResponseEntity<String> deleteSupport(@RequestParam("support-id") UUID supportId, @RequestParam("explain") String explain) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.manageSupportRequest(username, supportId, explain))
            return new ResponseEntity<>("Richiesta di supporto eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della richiesta di supporto", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
