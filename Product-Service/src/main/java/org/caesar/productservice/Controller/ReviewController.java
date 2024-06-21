package org.caesar.productservice.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.ReviewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product-api")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {


    private final ModelMapper modelMapper;
    private final ReviewService reviewService;
    private final ProductService productService;

    /*@PostMapping("/review")
    public ResponseEntity<String> addReview(@RequestBody ReviewDTO reviewDTO) {

        Product product = new Product();
        product.setId(productService.getProductIDByName(reviewDTO.getNameProduct()));

        UUID reviewID = reviewService.addOrUpdateReview(reviewDTO, product);

        if (reviewID != null) {
            return new ResponseEntity<>("Recensione aggiunta", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Recensione non aggiunta", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/review")
    public String ciao(){
        return "ciao";
    }*/
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
}
