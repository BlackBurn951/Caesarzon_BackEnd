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
            return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/admin/notifications")
    public ResponseEntity<List<AdminNotificationDTO>> getAllAdminNotifications() {
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        List<AdminNotificationDTO> result = adminNotificationService.getAdminNotification(username);

        if (result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.OK);
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

    @DeleteMapping("/notifications/{username}")
    public ResponseEntity<String> deleteNotifications(@PathVariable String username) {
        boolean result = userNotificationService.deleteAllUserNotification(username);
        if(result)
            return new ResponseEntity<>("Eliminata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
