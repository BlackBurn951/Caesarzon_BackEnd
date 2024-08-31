package org.caesar.productservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Utils.CallCenter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewOrchestrator {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final CallCenter callCenter;

    public boolean processDeleteReview(ReviewDTO reviewDTO) {

        //Fase di validazione in locale
        boolean validateReview= reviewService.validateDeleteReview(reviewDTO, false);

        if(validateReview) {

            //Fase di validazione sul servizio esterno
            DeleteReviewDTO validateRemote= callCenter.validateAndRollbackReportAndNotifications(reviewDTO.getUsername(), reviewDTO.getId(), false);

            if(validateRemote!=null) {

                //Fase di completamento in locale
                boolean completeReview = reviewService.completeDeleteReview(reviewDTO);

                //Caso in cui non ci siano segnalazioni a capo di questa recensione
                if(validateRemote.getReports().isEmpty() && completeReview) {
                    reviewService.releaseLock(List.of(reviewDTO.getId()));

                    return true;
                }

                if(completeReview) {

                    //Fase di completamento sul servizio esterno
                    boolean completeAdminNotify= callCenter.completeNotificationDelete(reviewDTO.getId());
                    boolean completeReport= callCenter.completeReportDelete(reviewDTO.getId());

                    if(completeAdminNotify && completeReport) {

                        //Fase di rilascio di tutti i lock
                        reviewService.releaseLock(List.of(reviewDTO.getId()));
                        callCenter.releaseNotificationLock(validateRemote.getAdminNotify().stream().map(SaveAdminNotificationDTO::getId).toList());
                        callCenter.releaseReportLock(validateRemote.getReports().stream().map(ReportDTO::getId).toList());

                        return true;
                    }

                    //Fase di rollback post completamento in locale e sul servizio esterno
                    rollbackPostCompleteLocal(reviewDTO);
                    rollbackPostCompleteRemote(validateRemote.getAdminNotify(), validateRemote.getReports());

                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(reviewDTO);
                rollbackPreCompleteRemote(reviewDTO.getUsername(), reviewDTO.getId());

                return false;
            }

            //Fase di rollback pre completamento in locale e sul servizio esterno
            rollbackPreCompleteRemote(reviewDTO.getUsername(), reviewDTO.getId());
        }

        //Fase di rollback pre completamento in locale
        rollbackPreCompleteLocal(reviewDTO);

        return false;
    }


    private boolean rollbackPreCompleteLocal(ReviewDTO reviewDTO) {
        return reviewService.validateDeleteReview(reviewDTO, true);
    }
    private boolean rollbackPreCompleteRemote(String username, UUID reviewId) {
        return callCenter.validateAndRollbackReportAndNotifications(username, reviewId, true)!=null;
    }
    private boolean rollbackPostCompleteLocal(ReviewDTO reviewDTO) {
        ProductDTO productDTO= productService.getProductById(reviewDTO.getId());

        if(productDTO==null)
            return false;
        return reviewService.addReview(reviewDTO, productDTO).endsWith("!");
    }
    private boolean rollbackPostCompleteRemote(List<SaveAdminNotificationDTO> adminNotifications, List<ReportDTO> reports) {
        return callCenter.rollbackReport(reports) && callCenter.rollbackNotifications(adminNotifications);
    }
}
