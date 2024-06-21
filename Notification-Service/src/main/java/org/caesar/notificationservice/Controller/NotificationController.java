package org.caesar.notificationservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.*;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.ReportResponseDTO;
import org.caesar.notificationservice.Dto.SupportDTO;
import org.caesar.notificationservice.Dto.SupportResponseDTO;
import org.caesar.notificationservice.Dto.NotificationDTO;
import org.caesar.notificationservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notify-api")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final HttpServletRequest httpServletRequest;
    private final GeneralService generalService;
    private final ReportService reportService;
    private final SupportRequestService supportRequestService;
    private final BanService banService;
    private final UserNotificationService userNotificationService;
    private final AdminNotificationService adminNotificationService;

    //End-point per la gestione delle notifiche
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(@RequestParam("user") int isUser) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        List<NotificationDTO> result;

        result= isUser==0? userNotificationService.getUserNotification(username): adminNotificationService.getAdminNotification(username);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/notification/{username}")
    public ResponseEntity<String> createNotification(@PathVariable String username, @RequestBody NotificationDTO notificationDTO) {
        if(userNotificationService.addUserNotification(notificationDTO, username))
            return new ResponseEntity<>("Notifica creata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella creazione della notifica...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @PutMapping("/notifications")
//    public ResponseEntity<String> updateNotifications(@RequestBody List<NotificationDTO> notificationDTO) {
//
//    }

    @DeleteMapping("/notification")
    public ResponseEntity<String> deleteNotification(@RequestBody NotificationDTO notificationDTO, @RequestParam("user") int isUser) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        boolean result;

        result = isUser==0? userNotificationService.deleteUserNotification(notificationDTO, username): adminNotificationService.deleteAdminNotification(notificationDTO, username);

        if(result)
            return new ResponseEntity<>("Eliminata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    //End-point per le segnalazioni
    @GetMapping("/report")
    public ResponseEntity<List<ReportDTO>> getReports(@RequestParam("num") int num) {
        List<ReportDTO> result = reportService.getAllReports(num);

        log.debug("Sono nell'end-point del get report");
        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/report")
     public ResponseEntity<String> sendReport(@RequestBody ReportDTO reportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        log.debug("Sono nell'end-point del send report");
        if(generalService.addReportRequest(username, reportDTO))
            return new ResponseEntity<>("Segnalazione inviata con sucesso!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'invio della segnalazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/report")
    public ResponseEntity<String> deleteReport(@RequestBody ReportResponseDTO reportResponseDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        log.debug("Sono nell'end-point del delete report");
        if(generalService.manageReport(reportResponseDTO, username))
            return new ResponseEntity<>("Segnalazione eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della segnalazione", HttpStatus.INTERNAL_SERVER_ERROR);

    }


    //End-point per le richieste di supporto
    @GetMapping("/support")
    public ResponseEntity<List<SupportDTO>> getSupports(@RequestParam("num") int num) {
        List<SupportDTO> result = supportRequestService.getAllSupportRequest(num);

        log.debug("Sono nell'end-point del get support");
        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/support")
    public ResponseEntity<String> sendReport(@RequestBody SupportDTO supportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        log.debug("Sono nell'end-point del send support");
        if(generalService.addSupportRequest(username, supportDTO))
            return new ResponseEntity<>("Richiesta di supporto inviata con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'invio della richiesta di supporto...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/support")
    public ResponseEntity<String> deleteSupport(@RequestBody SupportResponseDTO supportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        log.debug("Sono nell'end-point del delete support");
        if(generalService.manageSupportRequest(username, supportDTO))
            return new ResponseEntity<>("Richiesta di supporto eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della richiesta di supporto", HttpStatus.INTERNAL_SERVER_ERROR);

    }


    //End-point per lo sban
    @PutMapping("/ban/{username}")
    public ResponseEntity<String> sbanUser(@PathVariable("username") String username) {
        if(banService.sbanUser(username))
            return new ResponseEntity<>("Utente sbannato con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nello sban dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
