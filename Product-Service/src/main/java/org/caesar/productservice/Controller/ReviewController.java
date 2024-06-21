package org.caesar.productservice.Controller;

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

import java.util.List;
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
    public ResponseEntity<String> createReview(@RequestBody ReviewDTO reviewDTO) {
        Product product = new Product();
        product.setId(productService.getProductIDByName(reviewDTO.getNameProduct()));

        UUID reviewID = reviewService.addOrUpdateReview(reviewDTO, product);
        System.out.println("Review ID: " + reviewID);
        if (reviewID != null) {
            return new ResponseEntity<>("Recensione aggiunta", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Recensione non aggiunta", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //La get funziona
    @GetMapping("/review")
    public ResponseEntity<List<ReviewDTO>> getReview(@RequestParam String productName) {
        UUID productID = productService.getProductIDByName(productName);
        Product product = productService.getProductById(productID);
        System.out.println("Product ID: " + productID);
        List<ReviewDTO> reviewDTOS = reviewService.getReviewsByProductId(product);
        for(ReviewDTO reviewDTO : reviewDTOS) {
            System.out.println(reviewDTO);
        }
        if (!reviewDTOS.isEmpty()) {
            return new ResponseEntity<>(reviewDTOS, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/review")
    public ResponseEntity<ReviewDTO> deleteReview(@RequestParam String username) {

        Review review = reviewService.getReview(username);
        if (review != null) {
            reviewService.deleteReview(review.getId());
        }
        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        if (reviewDTO != null) {
            ReviewDTO response = restTemplate.postForObject(
                    "http://notification-service/notify-api/report",
                    reviewDTO,
                    ReviewDTO.class
            );
            return ResponseEntity.ok(response);
        } else
            return ResponseEntity.internalServerError().build();
    }
}
