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
        result.setReports(reportService.validateDeleteReportByUsername2(username, rollback));

        if(result.getReports()==null)
            result.setAdminNotificationForReport(null);
        else if(!result.getReports().isEmpty()) {

            for (ReportDTO report: result.getReports()) {
                if(rollback)
                    adminNotificationService.rollbackPreComplete(report);
                result.setAdminNotificationForReport(adminNotificationService.validateDeleteByReport(report));
            }
        }
        else
            result.setAdminNotificationForReport(new Vector<>());

        if(result.getSupports()==null)
            result.setAdminNotificationForSupport(null);
        else if(!result.getSupports().isEmpty()) {

            for (SupportDTO support: result.getSupports()) {
                result.setAdminNotificationForSupport(adminNotificationService.validateOrRollbackDeleteBySupports(support, rollback));
            }
        }
        else
            result.setAdminNotificationForSupport(new Vector<>());

        result.setUserNotification(userNotificationService.validateOrRollbackDeleteUserNotifications(username, rollback));

        if(result.getUserNotification()!=null && result.getAdminNotificationForReport()!=null && result.getAdminNotificationForSupport()!=null
                && result.getReports()!=null && result.getReports().isEmpty())
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user/delete")
    public ResponseEntity<CompleteUserDeleteDTO> completeUserDelete(@RequestBody ValidateUserDeleteDTO lists) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        CompleteUserDeleteDTO result= new CompleteUserDeleteDTO();

        boolean supportNotify= true,
                reportNotify= true;
        if(!lists.getSupports().isEmpty()) {
            result.setSupport(supportRequestService.completeDeleteUserSupport(username));


            for (SupportDTO support: lists.getSupports()) {
                supportNotify= adminNotificationService.completeDeleteBySupports(support);

                if(!supportNotify)
                    break;
            }
        }
        if(!lists.getReports().isEmpty() && supportNotify) {
            result.setReport(reportService.completeDeleteReportByUsername2(username));

            for (ReportDTO report: lists.getReports()) {
                reportNotify= adminNotificationService.completeDeleteByReport(report);

                if(!reportNotify)
                    break;
            }
        }
        if(lists.getUserNotification().isEmpty())
            result.setUserNotification(true);
        else
            result.setUserNotification(userNotificationService.completeDeleteUserNotifications(username));

        if(result.isUserNotification() &&
                ((!lists.getSupports().isEmpty() && result.isSupport() && supportNotify) ||
                (!lists.getReports().isEmpty() && result.isReport() && reportNotify)))
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> releaseLockUserDelete(@RequestBody ReleaseLockUserDeleteDTO lists, @RequestParam("support") boolean support) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        int report= 2, sup= 2, admin= 2, user= 2;
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

        if(lists.getUserNotification()!=null) {
            if(userNotificationService.releaseDeleteUserNotifications(username))
                user= 0;
            else
                user=1;
        }

        if( (user==2 || user==0) && (report==2 || report==0)
                && (sup==2 || sup==0) && (admin==2 || admin==0))
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
