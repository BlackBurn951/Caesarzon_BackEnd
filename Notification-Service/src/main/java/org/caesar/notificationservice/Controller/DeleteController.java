package org.caesar.notificationservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.AdminNotificationService;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Data.Services.SupportRequestService;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.caesar.notificationservice.Dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Vector;

@RestController
@RequestMapping("/notify-api")
@RequiredArgsConstructor
@Slf4j
public class DeleteController {

    private final AdminNotificationService adminNotificationService;
    private final UserNotificationService userNotificationService;
    private final ReportService reportService;
    private final SupportRequestService supportRequestService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("/user/delete")
    public ResponseEntity<ValidateUserDeleteDTO> validateUserDelete(@RequestParam("rollback") boolean rollback) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        ValidateUserDeleteDTO result= new ValidateUserDeleteDTO();

        result.setSupports(supportRequestService.validateOrRollbackDeleteUserSupport(username, rollback));
        result.setReports(reportService.validateDeleteReportForUserDelete(username, rollback));

        if(result.getReports()==null)
            result.setAdminNotificationForReport(1);
        else if(!result.getReports().isEmpty()) {
            boolean adminReports= true;

            for (ReportDTO report: result.getReports()) {
                if(rollback)
                    adminNotificationService.rollbackPreComplete(report);
                else if(!adminNotificationService.validateDeleteByReport(report))
                    adminReports = false;
            }

            if(adminReports)
                result.setAdminNotificationForReport(0);
            else
                result.setAdminNotificationForReport(1);
        }
        else
            result.setAdminNotificationForReport(2);

        if(result.getSupports()==null)
            result.setAdminNotificationForReport(1);
        else if(!result.getSupports().isEmpty()) {
            boolean adminSupport= true;

            for (SupportDTO support: result.getSupports()) {
                if(!adminNotificationService.validateOrRollbackDeleteBySupports(support, rollback))
                    adminSupport = false;
            }

            if(adminSupport)
                result.setAdminNotificationForSupport(0);
            else
                result.setAdminNotificationForSupport(1);
        }
        else
            result.setAdminNotificationForSupport(2);

        result.setUserNotification(userNotificationService.validateOrRollbackDeleteUserNotifications(username, rollback));

        if(result.isUserNotification() && result.getAdminNotificationForReport()!=1 && result.getAdminNotificationForSupport()!=1)
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user/delete")
    public ResponseEntity<CompleteUserDeleteDTO> completeUserDelete(@RequestBody ValidateUserDeleteDTO lists) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        CompleteUserDeleteDTO result= new CompleteUserDeleteDTO();

        List<SaveAdminNotificationDTO> adminNotification= new Vector<>();

        if(lists.getSupports()!=null) {
            result.setSupport(supportRequestService.completeDeleteUserSupport(username));

            for (ReportDTO report: lists.getReports()) {
                List<SaveAdminNotificationDTO> reportNotification= adminNotificationService.completeDeleteByReport(report);

                if(reportNotification!=null)
                    adminNotification.addAll(reportNotification);
                else {
                    adminNotification = null;
                    break;
                }
            }
        }
        if(lists.getReports()!=null && adminNotification!=null) {
            result.setReport(reportService.completeDeleteReportForUserDelete(username));

            for (SupportDTO support: lists.getSupports()) {
                List<SaveAdminNotificationDTO> supportNotification= adminNotificationService.completeDeleteBySupports(support);

                if(supportNotification!=null)
                    adminNotification.addAll(supportNotification);
                else {
                    adminNotification = null;
                    break;
                }
            }
        }
        result.setUserNotification(userNotificationService.completeDeleteUserNotifications(username));

        if(result.getUserNotification()!=null &&
                ((lists.getSupports()!=null && result.isSupport() && adminNotification!=null) ||
                (lists.getReports()!=null && result.isReport() && adminNotification!=null)))
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> releaseLockUserDelete(@RequestBody ReleaseLockUserDeleteDTO lists, @RequestParam("support") boolean support) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        int report= 2, sup= 2, admin= 2;
        if(lists.getReportId()!=null) {
            if(reportService.releaseLock(lists.getReportId()))
                report= 0;
            else
                report=1;
        }
        if(support) {
            if(supportRequestService.releaseDeleteUserSupport(username))
                sup=0;
            else
                sup=1;
        }
        if(lists.getAdminNotification()!=null) {
            if(adminNotificationService.releaseLock(lists.getAdminNotification()))
                admin= 0;
            else
                admin=1;
        }

        if(userNotificationService.releaseDeleteUserNotifications(username) &&
                (report==2 || report==0) && (sup==2 || sup==0) && (admin==2 || admin==0))
            return new ResponseEntity<>("Release avvenuto con successo", HttpStatus.OK);
        return new ResponseEntity<>("Problemi nel release dei lock", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/user/delete/rollback")
    public ResponseEntity<String> rollbackUserDelete(@RequestBody RollbackUserDeleteDTO rollback) {
        if(rollback.getReports()!=null) {
            for (ReportDTO report: rollback.getReports()) {
                reportService.addReport(report);
            }
        }
        if(rollback.getSupports()!=null)
            supportRequestService.rollbackDeleteUserSupport(rollback.getSupports());
        if(rollback.getAdminNotification()!=null)
            adminNotificationService.sendNotificationAllAdmin(rollback.getAdminNotification());

        userNotificationService.rollbackDeleteUserNotifications(rollback.getUserNotification());

        return new ResponseEntity<>("Rollback eseguito", HttpStatus.OK);
    }
}
