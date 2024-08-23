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

    //End-point per la creazione di una notifica all'utente

    //Validazione
    @PostMapping("/notification")
    public ResponseEntity<UUID> validateNotification() {
        UUID result= userNotificationService.validateNotification();

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Completamento
    @PutMapping("/notification")
    public ResponseEntity<String> completeNotification(@RequestBody UserNotificationDTO notificationDTO) {
        if(userNotificationService.completeNotification(notificationDTO))
            return new ResponseEntity<>("Completamento della notifica avvenuto con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problema nel completamento della notifica...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Rilascio lock
    @PutMapping("/notification")
    public ResponseEntity<String> releaseNotification(@RequestParam("notify-id") UUID notifyId) {
        if (userNotificationService.releaseNotification(notifyId))
            return new ResponseEntity<>("Release del lock avvenuto con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel release del lock...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Rollback
    @DeleteMapping("/notification/{notifyId}")
    public ResponseEntity<String> rollbackNotification(@PathVariable UUID notifyId) {
        if(userNotificationService.deleteUserNotification(notifyId))
            return new ResponseEntity<>("Rollback avvenuto con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problema nel rollback...", HttpStatus.INTERNAL_SERVER_ERROR);
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

    @DeleteMapping("/notifications/{username}")
    public ResponseEntity<String> deleteNotifications(@PathVariable String username) {
        boolean result = userNotificationService.deleteAllUserNotification(username);
        if(result)
            return new ResponseEntity<>("Eliminata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore", HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @PutMapping("/user/notifications/{reviewId}")
    public ResponseEntity<List<SaveAdminNotificationDTO>> completeNotifyDelete(@PathVariable UUID reviewId) {
        List<SaveAdminNotificationDTO> result= generalService.completeDeleteAdminNotifications(reviewId);

        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user/notifications")
    public ResponseEntity<String> releaseLock(@RequestBody List<UUID> notificationIds) {
        if(adminNotificationService.releaseLock(notificationIds))
            return new ResponseEntity<>("Lock rilasciato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel rilascio del lock...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user/notifications")
    public ResponseEntity<String> rollbackPreComplete(@RequestParam("review-id") UUID reviewId) {
        if(generalService.rollbackPreComplete(reviewId))
            return new ResponseEntity<>("Rollback eseguito con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'esecuzione del rollback...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/user/notifications")
    public ResponseEntity<String> rollbackPostComplete(@RequestBody List<SaveAdminNotificationDTO> notifications) {
        for(SaveAdminNotificationDTO adminNotification: notifications) {
            adminNotification.setConfirmed(true);
        }

        if(adminNotificationService.sendNotificationAllAdmin(notifications))
            return new ResponseEntity<>("Rollback eseguito con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'esecuzione del rollback...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
