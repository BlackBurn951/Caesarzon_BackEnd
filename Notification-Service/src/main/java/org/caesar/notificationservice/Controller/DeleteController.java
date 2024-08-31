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
    public ResponseEntity<String> validateUserDelete(@RequestParam("rollback") boolean rollback) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<SupportDTO> supports= supportRequestService.validateOrRollbackDeleteUserSupport(username, rollback);
        List<ReportDTO> reports= reportService.validateDeleteReportByUsername2(username, rollback);

        boolean adminNotification= true,
                userNotification= userNotificationService.validateOrRollbackDeleteUserNotifications(username, rollback);
        if(supports!=null && !supports.isEmpty()) {
            for(SupportDTO supportDTO : supports) {
                if(!adminNotificationService.validateOrRollbackDeleteBySupports(supportDTO, rollback)) {
                    adminNotification=false;
                    break;
                }
            }
        }

        if(reports!=null && !reports.isEmpty()) {
            for(ReportDTO reportDTO : reports) {
                if(!adminNotificationService.validateOrRollbackDeleteByReportsOnUserDelete(reportDTO, false)) {
                    adminNotification=false;
                    break;
                }
            }
        }

        if(supports!=null && reports!=null && adminNotification && userNotification)
            return new ResponseEntity<>("Validazione avvenuta con successo!", HttpStatus.OK);
        return new ResponseEntity<>("Problemi nella validazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
