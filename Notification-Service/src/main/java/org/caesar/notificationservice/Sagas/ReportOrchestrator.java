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
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class ReportOrchestrator {

    private final AdminNotificationService adminNotificationService;
    private final ReportService reportService;
    private final BanService banService;
    private final CallCenter callCenter;

    public boolean processAutomaticBan(UUID banId, String username, List<ReportDTO> reports) {
        //Fase di convalida in locale
        boolean validateAdminNotify= true,
                validateReport= reportService.validateDeleteReportByUsername2(username);

        for(ReportDTO report: reports){
            if(!adminNotificationService.validateDeleteByReport(report))
                validateAdminNotify= false;
        }

        if(validateAdminNotify && validateReport) {

            //Fase di convalida sui servizi esterni
            boolean validateReview= callCenter.validateReviewDeleteByUsername(username),
                    validateBan= callCenter.validateBan(username);

            if(validateReview && validateBan) {

                //Fase di completamento in locale
                BanDTO banDTO = generateBan(banId, username);

                boolean banConfirmed= banService.confirmBan(banDTO);

                List<SaveAdminNotificationDTO> adminNotifications= new Vector<>();
                for(ReportDTO report: reports){
                    List<SaveAdminNotificationDTO> temp= adminNotificationService.completeDeleteByReport(report);

                    if(temp!=null)
                        adminNotifications.addAll(temp);
                    else
                        adminNotifications= null;
                }

                List<ReportDTO> rollbackReports= reportService.completeDeleteReportByUsername2(username);

                if(banConfirmed && adminNotifications!=null && rollbackReports!=null) {

                    //Fase di completamento sui servizi esterni
                    boolean banCompleted= callCenter.completeBan(username);
                    List<ReviewDTO> reviews= callCenter.completeReviewDeleteByUsername(username);

                    if(banCompleted && reviews!=null) {

                        //Fase di rilascio dei lock su tutti i servizi
                        banService.releaseLock(banId);
                        adminNotificationService.releaseLock(adminNotifications.stream().map(SaveAdminNotificationDTO::getId).toList());
                        reportService.releaseLock(rollbackReports.stream().map(ReportDTO::getId).toList());
                        callCenter.releaseReviewLock(reviews.stream().map(ReviewDTO::getId).toList());
                        callCenter.releaseBanLock(username);
                        return true;
                    }

                    //Fase di rollback post completamento totale
                    rollbackPostCompleteLocal(banId, rollbackReports, adminNotifications);
                    rollbackPostCompleteRemote(username, reviews);
                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(banId, rollbackReports, adminNotifications);
                rollbackPreCompleteRemoteForUsername(username);
                return false;
            }

            //Fase di rollback pre completamento sui servizi esterni
            rollbackPreCompleteRemoteForUsername(username);
        }

        //Fase di rollback pre completamento in locale



        rollbackPreCompleteLocalForUsername(banId, reports, username);
        return false;
    }

    public boolean processManageReport(List<ReportDTO> reports) {
        //Fase di convalida in locale
        boolean validateAdminNotify= true,
                validateReport= reportService.validateDeleteReportByReview(reports.getFirst().getReviewId());

        for(ReportDTO report: reports){
            if(!adminNotificationService.validateDeleteByReport(report))
                validateAdminNotify= false;
        }

        if(validateAdminNotify && validateReport) {

            //Fase di convalida sul servizio esterno
            boolean validateReview= callCenter.validateReviewDeleteById(reports.getFirst().getReviewId(), false);  //eliminare singola recensione
            if(validateReview) {

                //Fase di completamento in locale
                List<SaveAdminNotificationDTO> adminNotifications= new Vector<>();
                for(ReportDTO report: reports){
                    List<SaveAdminNotificationDTO> temp= adminNotificationService.completeDeleteByReport(report);

                    if(temp!=null)
                        adminNotifications.addAll(temp);
                    else
                        adminNotifications= null;
                }

                List<ReportDTO> rollbackReports= reportService.completeDeleteReportByReview(reports.getFirst().getReviewId());
                if(adminNotifications!=null && rollbackReports!=null) {

                    //Fase di completamento sul servizio esterno
                    ReviewDTO reviews= callCenter.completeReviewDeleteById(reports.getFirst().getReviewId());
                    if(reviews!=null) {

                        //Fase di rilasciamento di tutti i lock
                        adminNotificationService.releaseLock(adminNotifications.stream().map(SaveAdminNotificationDTO::getId).toList());
                        reportService.releaseLock(rollbackReports.stream().map(ReportDTO::getId).toList());
                        callCenter.releaseReviewLock(List.of(reviews.getId()));

                        return true;
                    }

                    //Fase di rollback post completamento totale
                    rollbackPostCompleteLocal(null, rollbackReports, adminNotifications);
                    rollbackPostCompleteRemote(null, List.of(reviews));
                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(null, rollbackReports, adminNotifications);
                rollbackPreCompleteRemoteForReviewId(reports.getFirst().getReviewId());
                return false;
            }

            //Fase di rollback pre completaento sul servizio esterno
            rollbackPreCompleteRemoteForReviewId(reports.getFirst().getReviewId());
        }

        //Fase di rollback pre completamento in locale
        rollbackPreCompleteLocalForReviewId(reports);

        return false;
    }



    //Metodi di servizio
    private void rollbackPreCompleteLocalForUsername(UUID banId, List<ReportDTO> reports, String username) {
        banService.rollback(banId);
        for(ReportDTO report: reports){
            adminNotificationService.rollbackPreComplete(report);
        }
        reportService.rollbackPreCompleteByUsername2(username);
    }
    private void rollbackPreCompleteRemoteForUsername(String username) {
        callCenter.rollbackBan(username);
        callCenter.rollbackPreCompleteReviewDeleteByUsername(username);
    }
    private void rollbackPostCompleteLocal(UUID banId, List<ReportDTO> reports, List<SaveAdminNotificationDTO> adminNotifications) {
        if(banId!=null)
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
        if(username!=null)
            callCenter.rollbackBan(username);
    }

    private void rollbackPreCompleteLocalForReviewId(List<ReportDTO> reports) {
        for(ReportDTO report: reports){
            adminNotificationService.rollbackPreComplete(report);
        }
        reportService.rollbackPreCompleteByReview(reports.getFirst().getReviewId());
    }
    private void rollbackPreCompleteRemoteForReviewId(UUID reviewId) {
        callCenter.validateReviewDeleteById(reviewId, true);
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
