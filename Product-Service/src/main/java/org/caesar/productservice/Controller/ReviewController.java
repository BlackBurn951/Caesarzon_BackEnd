package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.ReviewDTO;
import org.modelmapper.ModelMapper;
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


    private final ModelMapper modelMapper;
    private final ReviewService reviewService;
    private final ProductService productService;
    private final RestTemplate restTemplate;

    //La post funziona
    @PostMapping("/review")
    public ResponseEntity<String> addReview(@RequestBody ReviewDTO reviewDTO) {

        UUID reviewID = reviewService.addOrUpdateReview(reviewDTO);
        System.out.println("Review ID: " + reviewID);
        if (reviewID != null) {
            return new ResponseEntity<>("Recensione aggiunta", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Recensione non aggiunta", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //La get funziona
    @GetMapping("/review")
    public ResponseEntity<List<ReviewDTO>> getReview(@RequestParam UUID productID) {
        List<ReviewDTO> reviewDTOS = reviewService.getReviewsByProductId(productID);
        for(ReviewDTO reviewDTO : reviewDTOS) {
            System.out.println(reviewDTO);
        }
        if (!reviewDTOS.isEmpty()) {
            return new ResponseEntity<>(reviewDTOS, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //Funziona, ma da il risultaato sbagliato
    @DeleteMapping("/review")
    public ResponseEntity<String> deleteReview(@RequestParam String username, @RequestParam UUID productID){

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

                System.out.println("Sono in questa risposta, la giusta");
                return new ResponseEntity<>("Recensione eliminata con sucesso!", HttpStatus.OK);
            }
        }
        System.out.println("Sono in questa risposta, la sbagliata");
        return  new ResponseEntity<>("Problemi nell'eliminazione!", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @DeleteMapping("/admin/review")
    public ResponseEntity<String> deleteReview(@RequestParam("review_id") UUID review_id) {

        if(review_id == null) {
            return new ResponseEntity<>("Recensione non trovata", HttpStatus.NOT_FOUND);
        }else {
            reviewService.deleteReview(review_id);
            return new ResponseEntity<>("Recensione trovata", HttpStatus.OK);
        }
    }
}
