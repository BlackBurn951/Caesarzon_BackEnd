package org.caesar.notificationservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.SendReportDTO;
import org.caesar.notificationservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/notity-api")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final HttpServletRequest httpServletRequest;
    private final GeneralService generalService;


    @PostMapping("/report")
    public ResponseEntity<String> sendReport(@RequestBody SendReportDTO reportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.addReportRequest(username, reportDTO))
            return new ResponseEntity<>("Segnalazione inviata con sucesso!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'invio della segnalazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


//    @PostMapping("/help")
}
