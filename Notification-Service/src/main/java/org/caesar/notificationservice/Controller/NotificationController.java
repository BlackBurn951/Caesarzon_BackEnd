package org.caesar.notificationservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.*;
import org.caesar.notificationservice.Dto.*;
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
public class NotificationController {

    private final HttpServletRequest httpServletRequest;
    private final GeneralService generalService;
    private final ReportService reportService;
    private final SupportRequestService supportRequestService;
    private final BanService banService;
    private final UserNotificationService userNotificationService;
    private final AdminNotificationService adminNotificationService;

    //End-point per la gestione delle notifiche
    @GetMapping("/user/notifications")
    public ResponseEntity<List<UserNotificationDTO>> getAllUserNotifications() {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<UserNotificationDTO> result= userNotificationService.getUserNotification(username);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/admin/notifications")
    public ResponseEntity<List<AdminNotificationDTO>> getAllAdminNotifications() {
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        List<AdminNotificationDTO> result = adminNotificationService.getAdminNotification(username);

        if (result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/notification")
    public ResponseEntity<String> createNotification(@RequestBody UserNotificationDTO notificationDTO) {
        if(userNotificationService.addUserNotification(notificationDTO))
            return new ResponseEntity<>("Notifica creata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella creazione della notifica...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user/notifications")
    public ResponseEntity<String> updateUserNotifications(@RequestBody List<UserNotificationDTO> notificationDTO) {
        if(userNotificationService.updateUserNotification(notificationDTO))
            return new ResponseEntity<>("Notifiche aggiornate con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiornamento della notifica...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/admin/notifications")
    public ResponseEntity<String> updateAdminNotifications(@RequestBody List<AdminNotificationDTO> notificationDTO) {
        if(generalService.updateAdminNotification(notificationDTO))
            return new ResponseEntity<>("Notifiche aggiornate con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiornamento della notifica...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/notification")
    public ResponseEntity<String> deleteNotification(@RequestParam("notify-id") UUID id, @RequestParam("isUser") boolean isUser) {
        boolean result;
        if(isUser) {
            result = userNotificationService.deleteUserNotification(id);
        }else{
            result = adminNotificationService.deleteAdminNotification(id);
        }
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



    @DeleteMapping("/admin/report")
    public ResponseEntity<String> deleteReport(@RequestParam("review_id") UUID reviewId, @RequestParam("accept") boolean accept) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.manageReport(username, reviewId, false, accept))
            return new ResponseEntity<>("Segnalazione eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della segnalazione", HttpStatus.INTERNAL_SERVER_ERROR);

    }


    //chiamata da product service
    @DeleteMapping("/user/report")
    public ResponseEntity<String> deleteReportFromProduct(@RequestParam("review_id") UUID reviewId) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.manageReport(username, reviewId, true, true))
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

        if(generalService.addSupportRequest(username, supportDTO))
            return new ResponseEntity<>("Richiesta di supporto inviata con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'invio della richiesta di supporto...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/support")
    public ResponseEntity<String> deleteSupport(@RequestBody SupportResponseDTO supportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

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

    //End-point per lo sban
    @PostMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody BanDTO banDTO) {
        if(banService.banUser(banDTO))
            return new ResponseEntity<>("Utente sbannato con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nello sban dell'user...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
