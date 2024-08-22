package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.AverageDTO;
import org.caesar.productservice.Dto.ReviewDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product-api")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {


    private final ReviewService reviewService;
    private final HttpServletRequest httpServletRequest;
    private final GeneralService generalService;

    @GetMapping("/review/{id}") //End-point per prendere il testo della singola recensione
    public ResponseEntity<String> getReview(@PathVariable UUID id) {
        String reviewText = reviewService.getTextReview(id);

        if (!reviewText.isEmpty()) {
            return new ResponseEntity<>(reviewText, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/review/average")
    public ResponseEntity<AverageDTO> getReviewAverage(@RequestParam("prod-id") UUID productID) {
        AverageDTO averageDTO = generalService.getReviewAverage(productID);

        if(averageDTO != null)
            return new ResponseEntity<>(averageDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Endpoint per ottenere la lista di tutte le recensioni di un prodotto tramite il suo id
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(@RequestParam("prod-id") UUID productID, @RequestParam("str") int str) {
        List<ReviewDTO> reviewDTOS = generalService.getProductReviews(productID, str);

        if (reviewDTOS==null || reviewDTOS.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(reviewDTOS, HttpStatus.OK);
        }
    }

    @GetMapping("/reviews/score")
    public ResponseEntity<List<Integer>> getReviewsScore(@RequestParam("prod-id") UUID productID) {
        List<Integer> result= generalService.getReviewScore(productID);

        if(result != null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Endpoint per l'aggiunta di una recensione
    @PostMapping("/review")
    public ResponseEntity<String> addReview(@RequestBody ReviewDTO reviewDTO) {
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        String result= generalService.addReview(reviewDTO, username);
        if(result.endsWith("!"))
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Endpoint per l'eliminazione di una recensione e delle eventuali segnalazioni ad essa collegate
    @DeleteMapping("/review")
    public ResponseEntity<String> deleteReviewByUser(@RequestParam("product-id") UUID productID){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.deleteReviewByUser(username, productID))
            return new ResponseEntity<>("Recensione eliminata con sucesso!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'eliminazione della recensione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Endpoint per l'eliminazione della recensione tramite id
    @PutMapping("/admin/review")
    public ResponseEntity<String> validateDeleteReview(@RequestParam("review-id") UUID reviewId, @RequestParam("rollback") boolean rollback) {
        if(reviewService.validateDeleteReviewById(reviewId, rollback))
            return new ResponseEntity<>("Validazione dell'eliminazione avvenuta con sucesso!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella validazione dell'elimazione delle recensioni...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/admin/review/review-id/{reviewId}")
    public ResponseEntity<ReviewDTO> completeDeleteReview(@PathVariable UUID reviewId) {
        ReviewDTO result= reviewService.completeDeleteReviewById(reviewId);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    // Endpoint per l'eliminazione della recensione tramite username
    @PutMapping("/admin/review")
    public ResponseEntity<String> validateDeleteReviews(@RequestParam("username") String username, @RequestParam("rollback") boolean rollback) {
        if(reviewService.validateDeleteReviews(username, rollback))
            return new ResponseEntity<>("Validazione dell'eliminazione avvenuta con sucesso!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella validazione dell'elimazione delle recensioni...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/admin/review/{username}")
    public ResponseEntity<List<ReviewDTO>> completeDeleteReviews(@PathVariable String username) {
        List<ReviewDTO> result= reviewService.completeDeleteReviews(username);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/admin/review")
    public ResponseEntity<String> releaseLock(@RequestBody List<UUID> reviewId) {;
        if(reviewService.releaseLock(reviewId))
            return new ResponseEntity<>("Rilascio del lock avvenuto con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel rilascio del lock...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/admin/review")
    public ResponseEntity<String> rollbackDeleteReviews(@RequestBody List<ReviewDTO> reviews) {
        for(ReviewDTO review: reviews) {
            if(!generalService.addReview(review, review.getUsername()).endsWith("!"))
                return new ResponseEntity<>("Problemi nel rollback...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Rollback eseguito con successo!", HttpStatus.OK);
    }
}
