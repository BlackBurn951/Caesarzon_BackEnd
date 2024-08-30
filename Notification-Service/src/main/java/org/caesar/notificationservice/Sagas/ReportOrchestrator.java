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
        List<SaveAdminNotificationDTO> validateAdminNotify= new Vector<>();
        List<ReportDTO> validateReport= reportService.validateDeleteReportByUsername2(username, false);

        for(ReportDTO report: reports){
            validateAdminNotify.addAll(adminNotificationService.validateDeleteByReport(report, false));
        }

        if(!validateAdminNotify.isEmpty() && !validateReport.isEmpty()) {

            //Fase di convalida sui servizi esterni
            List<ReviewDTO> validateReview= callCenter.validateAndRollbackReviewDeleteByUsername(username, false);
            boolean validateBan= callCenter.validateBan(username);

            if(validateReview!=null && validateBan) {

                //Fase di completamento in locale
                BanDTO banDTO = generateBan(banId, username);

                boolean banConfirmed= banService.confirmBan(banDTO),
                        completeNotification= true,
                        completeReports= true;

                for(ReportDTO report: reports){
                    if(!adminNotificationService.completeDeleteByReport(report)) {
                        completeNotification= false;
                        break;
                    }
                }

                completeReports= reportService.completeDeleteReportByUsername2(username);

                if(banConfirmed && completeNotification && completeReports) {

                    //Fase di completamento sui servizi esterni
                    boolean banCompleted= callCenter.completeBan(username),
                            completeReviews= callCenter.completeReviewDeleteByUsername(username);

                    if(banCompleted && completeReviews) {

                        //Fase di rilascio dei lock su tutti i servizi
                        banService.releaseLock(banId);
                        adminNotificationService.releaseLock(validateAdminNotify.stream().map(SaveAdminNotificationDTO::getId).toList());
                        reportService.releaseLock(validateReport.stream().map(ReportDTO::getId).toList());
                        callCenter.releaseReviewLock(validateReview.stream().map(ReviewDTO::getId).toList());
                        callCenter.releaseBanLock(username);
                        return true;
                    }

                    //Fase di rollback post completamento totale
                    rollbackPostCompleteLocal(banId, validateReport, validateAdminNotify);
                    rollbackPostCompleteRemote(username, validateReview);
                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(banId, validateReport, validateAdminNotify);
                rollbackPreCompleteRemoteForUsername(username);
                return false;
            }

            //Fase di rollback pre completamento sui servizi esterni
            rollbackPreCompleteRemoteForUsername(username);
        }

        //Fase di rollback pre completamento in locale
        rollbackPreCompleteLocalForUsername(banId, reports);
        return false;
    }

    public boolean processManageReport(List<ReportDTO> reports) {
        //Fase di convalida in locale
        List<SaveAdminNotificationDTO> validateAdminNotify= new Vector<>();
        List<ReportDTO> validateReport= reportService.validateDeleteReportByReview(reports.getFirst().getReviewId(), false);

        for(ReportDTO report: reports){
            validateAdminNotify.addAll(adminNotificationService.validateDeleteByReport(report, false));
        }

        if(!validateAdminNotify.isEmpty() && !validateReport.isEmpty()) {

            //Fase di convalida sul servizio esterno
            ReviewDTO validateReview= callCenter.validateReviewDeleteById(reports.getFirst().getReviewId(), false);  //eliminare singola recensione
            if(validateReview!=null) {

                //Fase di completamento in locale
                boolean completeAdminNotification= true;
                for(ReportDTO report: reports){
                    if(!adminNotificationService.completeDeleteByReport(report)) {
                        completeAdminNotification= false;
                        break;
                    }
                }
                System.out.println("Completamento in locale delle notifiche terminato");
                boolean rollbackReports= reportService.completeDeleteReportByReview(reports.getFirst().getReviewId());
                System.out.println(completeAdminNotification+" "+rollbackReports);
                if(completeAdminNotification && rollbackReports) {
                    System.out.println("Completamento in locale terminato");
                    //Fase di completamento sul servizio esterno
                    boolean completeReviews= callCenter.completeReviewDeleteById(reports.getFirst().getReviewId());
                    if(completeReviews) {

                        //Fase di rilasciamento di tutti i lock
                        adminNotificationService.releaseLock(validateAdminNotify.stream().map(SaveAdminNotificationDTO::getId).toList());
                        reportService.releaseLock(validateReport.stream().map(ReportDTO::getId).toList());
                        callCenter.releaseReviewLock(List.of(validateReview.getId()));

                        return true;
                    }

                    //Fase di rollback post completamento totale
                    rollbackPostCompleteLocal(null, validateReport, validateAdminNotify);
                    rollbackPostCompleteRemote(null, List.of(validateReview));
                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(null, validateReport, validateAdminNotify);
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
    private void rollbackPreCompleteLocalForUsername(UUID banId, List<ReportDTO> reports) {
        banService.rollback(banId);
        for(ReportDTO report: reports){
            adminNotificationService.rollbackPreComplete(report);
            reportService.addReport(report);
        }
    }
    private void rollbackPreCompleteRemoteForUsername(String username) {
        callCenter.rollbackBan(username);
        callCenter.validateAndRollbackReviewDeleteByUsername(username, true);
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
        reportService.validateDeleteReportByReview(reports.getFirst().getReviewId(), true);
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
