package org.caesar.notificationservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.ReportDTO;
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
public class ReportController {

    private final HttpServletRequest httpServletRequest;
    private final GeneralService generalService;
    private final ReportService reportService;

    @GetMapping("/report")
    public ResponseEntity<List<ReportDTO>> getReports(@RequestParam("num") int num) {
        List<ReportDTO> result = reportService.getAllReports(num);
        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/report")
    public ResponseEntity<String> sendReport(@RequestBody ReportDTO reportDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        if(generalService.addReportRequest(username, reportDTO))
            return new ResponseEntity<>("Segnalazione inviata con sucesso!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'invio della segnalazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //End-point per la gestione della segnalazione da parte dell'admin
    @DeleteMapping("/admin/report")
    public ResponseEntity<String> deleteReport(@RequestParam("review_id") UUID reviewId, @RequestParam("accept") boolean accept) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.manageReport(username, reviewId, false, accept))
            return new ResponseEntity<>("Segnalazione eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della segnalazione", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    //End-point per la cancellazione di eventuali segnalazioni e notifiche inerenti ad una notifica
    @DeleteMapping("/user/report")
    public ResponseEntity<String> deleteReportFromProduct(@RequestParam("review_id") UUID reviewId) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.manageReport(username, reviewId, true, true))
            return new ResponseEntity<>("Segnalazione eliminata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della segnalazione", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
