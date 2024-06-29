package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.AverageDTO;
import org.caesar.productservice.Dto.ReviewDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/product-api")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {


    private final ReviewService reviewService;
    private final RestTemplate restTemplate;
    private final HttpServletRequest httpServletRequest;

    // Endpoint per l'aggiunta di una recensione
    @PostMapping("/review")
    public ResponseEntity<String> addReview(@RequestBody ReviewDTO reviewDTO) {
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        UUID reviewID = reviewService.addReview(reviewDTO, username);
        if (reviewID != null) {
            return new ResponseEntity<>("Recensione aggiunta", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Recensione non aggiunta", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<String> getReview(@PathVariable UUID id) {
        String testoReview = reviewService.getTextReview(id);
        if (!testoReview.isEmpty()) {
            return new ResponseEntity<>(testoReview, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Endpoint per ottenere la lista di tutte le recensioni di un prodotto tramite il suo id
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(@RequestParam UUID productID) {
        List<ReviewDTO> reviewDTOS = reviewService.getReviewsByProductId(productID);
        if (!reviewDTOS.isEmpty()) {
            return new ResponseEntity<>(reviewDTOS, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Endpoint per l'eliminazione di una recensione e delle eventuali segnalazioni ad essa collegate
    @DeleteMapping("/review")
    public ResponseEntity<String> deleteReviewByUser(@RequestParam("product-id") UUID productID){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        UUID reviewID = reviewService.getReviewIDByUsernameAndProductID(username, productID);
        if (reviewID != null) {
            reviewService.deleteReview(reviewID);
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", request.getHeader("Authorization"));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            boolean responseEntity = restTemplate.exchange(
                    "http://notification-service/notify-api/user/report?review_id="+reviewID,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            ).getStatusCode() == HttpStatus.OK;

            if(responseEntity){
                return new ResponseEntity<>("Recensione eliminata con sucesso!", HttpStatus.OK);
            }
        }
        return  new ResponseEntity<>("Problemi nell'eliminazione!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Endpoint per l'eliminazione della recensione tramite id
    @DeleteMapping("/admin/review")
    public ResponseEntity<String> deleteReviewById(@RequestParam("review_id") UUID review_id) {

        if(review_id == null) {
            return new ResponseEntity<>("Recensione non trovata", HttpStatus.NOT_FOUND);
        }else {
            reviewService.deleteReview(review_id);
            return new ResponseEntity<>("Recensione trovata", HttpStatus.OK);
        }
    }

    @GetMapping("/review/average")
    public ResponseEntity<AverageDTO> getReviewAverage(@RequestParam UUID productID) {

        if(productID != null) {
            AverageDTO averageDTO = reviewService.getReviewAverage(productID);
            if(averageDTO != null){
                return new ResponseEntity<>(averageDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
