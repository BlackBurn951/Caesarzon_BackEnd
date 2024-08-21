package org.caesar.notificationservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Services.AdminNotificationService;
import org.caesar.notificationservice.Data.Services.BanService;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.BanDTO;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.ReviewDTO;
import org.caesar.notificationservice.Dto.SaveAdminNotificationDTO;
import org.caesar.notificationservice.Utils.CallCenter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportOrchestrator {

    private final AdminNotificationService adminNotificationService;
    private final ReportService reportService;
    private final BanService banService;
    private final CallCenter callCenter;

    public boolean processAutomaticBan(UUID banId, ReportDTO reportDTO) {
        //Fase di convalida in locale
        boolean validateAdminNotify= adminNotificationService.validateDeleteByReport(reportDTO),
                validateReport= reportService.validateDeleteReport(reportDTO.getReviewId());

        if(validateAdminNotify && validateReport) {

            //Fase di convalida sui servizi esterni
            boolean validateReview= callCenter.validateReviewDelete(reportDTO.getUsernameUser2()),
                    validateBan= callCenter.validateBan(reportDTO.getUsernameUser2());

            if(validateReview && validateBan) {

                //Fase di completamento in locale
                BanDTO banDTO = generateBan(banId, reportDTO.getUsernameUser2());

                boolean banConfirmed= banService.confirmBan(banDTO);
                List<SaveAdminNotificationDTO> adminNotifications= adminNotificationService.completeDeleteByReport(reportDTO);
                List<ReportDTO> reports= reportService.completeDeleteReport(reportDTO.getReviewId());

                if(banConfirmed && adminNotifications!=null && reports!=null) {

                    //Fase di completamento sui servizi esterni
                    boolean banCompleted= callCenter.completeBan(reportDTO.getUsernameUser2());
                    List<ReviewDTO> reviews= callCenter.completeReviewDelete(reportDTO.getUsernameUser2());

                    if(banCompleted && reviews!=null) {

                        //Fase di rilascio dei lock su tutti i servizi
                        banService.releaseLock(banId);
                        adminNotificationService.releaseLock(adminNotifications.stream().map(SaveAdminNotificationDTO::getId).toList());
                        reportService.releaseLock(reports.stream().map(ReportDTO::getId).toList());
                        callCenter.releaseReviewLock(reviews.stream().map(ReviewDTO::getId).toList());
                        callCenter.releaseBanLock(reportDTO.getUsernameUser2());
                        return true;
                    }

                    //Fase di rollback post completamento totale
                    rollbackPostCompleteLocal(banId, reports, adminNotifications);
                    rollbackPostCompleteRemote(reportDTO.getUsernameUser2(), reviews);
                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(banId, reports, adminNotifications);
                rollbackPreCompleteRemote(reportDTO.getUsernameUser2());
                return false;
            }

            //Fase di rollback pre completamento sui servizi esterni
            rollbackPreCompleteRemote(reportDTO.getUsernameUser2());
        }

        //Fase di rollback pre completamento in locale
        rollbackPreCompleteLocal(banId, reportDTO);
        return false;
    }


    //Metodi di servizio
    private void rollbackPreCompleteLocal(UUID banId, ReportDTO reportDTO) {
        banService.rollback(banId);
        adminNotificationService.rollbackPreComplete(reportDTO);
        reportService.rollbackPreComplete(reportDTO.getReviewId());
    }

    private void rollbackPreCompleteRemote(String username) {
        callCenter.rollbackPreCompleteReviewDelete(username);
        callCenter.rollbackBan(username);
    }

    private void rollbackPostCompleteLocal(UUID banId, List<ReportDTO> reports, List<SaveAdminNotificationDTO> adminNotifications) {
        banService.rollback(banId);
        for(ReportDTO report: reports) {
            report.setEffective(true);
            reportService.addReport(report);
        }
        for(SaveAdminNotificationDTO adminNotification: adminNotifications) {
            adminNotification.setConfirmed(true);
        }
        adminNotificationService.sendNotificationAllAdmin(adminNotifications);
    }

    private void rollbackPostCompleteRemote(String username, List<ReviewDTO> reviews) {
        callCenter.rollbackReviewDelete(reviews);
        callCenter.rollbackBan(username);
    }

    private BanDTO generateBan(UUID banId, String username) {
        BanDTO banDTO= new BanDTO();

        banDTO.setId(banId);
        banDTO.setAdminUsername("System");
        banDTO.setReason("Limite di segnalazioni raggiunto");
        banDTO.setStartDate(LocalDate.now());
        banDTO.setEndDate(null);
        banDTO.setUserUsername(username);
        banDTO.setConfirmed(false);

        return banDTO;
    }
}
