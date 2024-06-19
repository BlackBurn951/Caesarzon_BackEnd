package org.caesar.notificationservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Data.Services.SupportRequestService;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.SupportDTO;
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


    @GetMapping("/report")
    public ResponseEntity<List<ReportDTO>> getReports(@RequestParam("num") int num) {

        List<ReportDTO> result = reportService.getAllReports(num);

        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/report")
    public ResponseEntity<String> sendReport(@RequestBody ReportDTO reportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        System.out.println("Sono nell'end-point delle notifiche");
        if(generalService.addReportRequest(username, reportDTO))
            return new ResponseEntity<>("Segnalazione inviata con sucesso!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'invio della segnalazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/report")
    public ResponseEntity<String> deleteReport(@RequestBody ReportDTO reportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.manageReport(username, reportDTO))
            return new ResponseEntity<>("Segnalazione eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della segnalazione", HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @GetMapping("/support")
    public ResponseEntity<List<SupportDTO>> getSupports(@RequestParam("num") int num) {

        List<SupportDTO> result = supportRequestService.getAllSupportRequest(num);

        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<String> deleteSupport(@RequestBody SupportDTO supportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.manageSupportRequest(username, supportDTO))
            return new ResponseEntity<>("Richiesta di supporto eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della richiesta di supporto", HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
