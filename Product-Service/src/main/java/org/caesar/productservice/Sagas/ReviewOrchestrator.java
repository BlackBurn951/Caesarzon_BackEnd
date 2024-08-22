package org.caesar.productservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ReportDTO;
import org.caesar.productservice.Dto.ReviewDTO;
import org.caesar.productservice.Dto.SaveAdminNotificationDTO;
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
            int validateRemote= callCenter.validateReportAndNotifications(reviewDTO.getUsername(), reviewDTO.getId());

            if(validateRemote==0 || validateRemote==1) {

                //Fase di completamento in locale
                boolean completeReview = reviewService.completeDeleteReview(reviewDTO);

                //Caso in cui non ci siano segnalazioni a capo di questa recensione
                if(validateRemote==1 && completeReview) {
                    reviewService.releaseLock(List.of(reviewDTO.getId()));

                    return true;
                }

                if(completeReview) {

                    //Fase di completamento sul servizio esterno
                    List<SaveAdminNotificationDTO> adminNotifications= callCenter.completeNotificationDelete(reviewDTO.getId());
                    List<ReportDTO> reports= callCenter.completeReportDelete(reviewDTO.getId());

                    if(adminNotifications!=null && reports!=null) {

                        //Fase di rilascio di tutti i lock
                        reviewService.releaseLock(List.of(reviewDTO.getId()));
                        callCenter.releaseReportLock(reports.stream().map(ReportDTO::getId).toList());
                        callCenter.releaseNotificationLock(adminNotifications.stream().map(SaveAdminNotificationDTO::getId).toList());

                        return true;
                    }

                    //Fase di rollback post completamento in locale e sul servizio esterno
                    rollbackPostCompleteLocal(reviewDTO);
                    rollbackPostCompleteRemote(adminNotifications, reports);

                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackPostCompleteLocal(reviewDTO);
                rollbackPreCompleteRemote(reviewDTO.getId());

                return false;
            }

            //Fase di rollback pre completamento in locale e sul servizio esterno
            rollbackPreCompleteRemote(reviewDTO.getId());
        }

        //Fase di rollback pre completamento in locale
        rollbackPreCompleteLocal(reviewDTO);

        return false;
    }


    private boolean rollbackPreCompleteLocal(ReviewDTO reviewDTO) {
        return reviewService.validateDeleteReview(reviewDTO, true);
    }
    private boolean rollbackPreCompleteRemote(UUID reviewId) {
        return callCenter.rollbackPreComplete(reviewId);
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
