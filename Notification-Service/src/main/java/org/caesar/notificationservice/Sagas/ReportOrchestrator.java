package org.caesar.notificationservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Services.AdminNotificationService;
import org.caesar.notificationservice.Data.Services.BanService;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.ReviewDTO;
import org.caesar.notificationservice.Dto.SaveAdminNotificationDTO;
import org.caesar.notificationservice.Utils.CallCenter;
import org.springframework.stereotype.Service;

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
        if(adminNotificationService.validateDeleteByReport(reportDTO) && reportService.validateDeleteReport(reportDTO.getReviewId())) {

            //Fase di convalida sui servizi esterni
            if(callCenter.validateReviewDelete(reportDTO.getUsernameUser2()) && callCenter.validateBan(reportDTO.getUsernameUser2())) {

                //Fase di completamento in locale
                boolean banConfirmed= banService.confirmBan(banId);
                List<SaveAdminNotificationDTO> adminNotifications= adminNotificationService.completeDeleteByReport(reportDTO);
                List<ReportDTO> reports= reportService.completeDeleteReport(reportDTO.getReviewId());
                if(banConfirmed && adminNotifications!=null && reports!=null) {

                    //Fase di completamento sui servizi esterni
                    boolean banCompleted= callCenter.completeBan(reportDTO.getUsernameUser2());
                    List<ReviewDTO> reviews= callCenter.completeReviewDelete(reportDTO.getUsernameUser2());
                    if(banCompleted && reviews!=null)
                        return true;

                    //Fase di rollback post completamento totale
                    rollbackPostCompleteLocal(reportDTO.getUsernameUser2(), reports, adminNotifications);
                    rollbackPostCompleteRemote(reportDTO.getUsernameUser2(), reviews);
                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(reportDTO.getUsernameUser2(), reports, adminNotifications);
                rollbackPreCompleteRemote(reportDTO.getUsernameUser2());
                return false;
            }

            //Fase di rollback pre completamento sui servizi esterni
            rollbackPreCompleteRemote(reportDTO.getUsernameUser2());
        }

        //Fase di rollback pre completamento in locale
        rollbackPreCompleteLocal(reportDTO);
        return false;
    }


    //Metodi di servizio
    private void rollbackPreCompleteLocal(ReportDTO reportDTO) {
        banService.rollback(reportDTO.getUsernameUser2());
        adminNotificationService.rollbackPreComplete(reportDTO);
        reportService.rollbackPreComplete(reportDTO.getReviewId());
    }

    private void rollbackPreCompleteRemote(String username) {
        callCenter.rollbackPreCompleteReviewDelete(username);
        callCenter.rollbackBan(username);
    }

    private void rollbackPostCompleteLocal(String username, List<ReportDTO> reports, List<SaveAdminNotificationDTO> adminNotifications) {
        banService.rollback(username);
        for(ReportDTO report: reports) {
            report.setEffective(true);
            reportService.addReport(report);
        }
        adminNotificationService.sendNotificationAllAdmin(adminNotifications);
    }

    private void rollbackPostCompleteRemote(String username, List<ReviewDTO> reviews) {
        callCenter.rollbackReviewDelete(reviews);
        callCenter.rollbackBan(username);
    }
}
