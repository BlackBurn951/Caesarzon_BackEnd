package org.caesar.productservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Dao.ReviewRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.AverageDTO;
import org.caesar.productservice.Dto.ReviewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final static String REVIEW_SERVICE = "reviewService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su reviewService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }


    @Override
//    @CircuitBreaker(name= REVIEW_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= REVIEW_SERVICE)
    public UUID addReview(ReviewDTO reviewDTO, String username) {
        if(reviewDTO == null){
            return null;
        }
        try {
            Review review = new Review();
            UUID productID = reviewDTO.getProductID();
            reviewDTO.setUsername(username);
            Product product = productRepository.findById(productID).orElse(null);
            if(product != null){
                review.setProduct(product);
                review.setDate(LocalDate.now());
                review.setText(reviewDTO.getText());
                review.setEvaluation(reviewDTO.getEvaluation());
                review.setUsername(reviewDTO.getUsername());

            }else{
                return null;
            }
            return reviewRepository.save(review).getId();

        }catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento della recensione");
            return null;
        }
    }

    @Override
//    @Retry(name= REVIEW_SERVICE)
    public Review getReviewById(UUID reviewID) {

        return reviewRepository.findById(reviewID).orElse(null);
    }

    @Override
//    @Retry(name= REVIEW_SERVICE)
    public UUID getReviewIDByUsernameAndProductID(String username, UUID productID) {
        Product product = productRepository.findById(productID).orElse(null);
        return reviewRepository.findReviewByUsernameAndProduct(username, product).getId();
    }


    @Override
//    @Retry(name= REVIEW_SERVICE)
    public List<ReviewDTO> getReviewsByProductId(UUID productID) {
        Product product = productRepository.findById(productID).orElse(null);
        List<ReviewDTO> reviewDTOS = new ArrayList<>();

        for(Review review: reviewRepository.findByproduct(product)){
            ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
            System.out.println(reviewDTO.getText());
            reviewDTOS.add(reviewDTO);
        }
        return reviewDTOS;
    }


    @Override
//    @CircuitBreaker(name= REVIEW_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= REVIEW_SERVICE)
    public boolean deleteReview(UUID id) {
        try {
            reviewRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della recensione");
            return false;
        }
    }


    @Override
//    @Retry(name= REVIEW_SERVICE)
    public AverageDTO getReviewAverage(UUID productID) {
        Product product = productRepository.findById(productID).orElse(null);
        if(product != null){
            List<Review> reviewDTOS = reviewRepository.findByproduct(product);
            double average = 0;
            for(Review review : reviewDTOS){
                average += review.getEvaluation();
            }
            AverageDTO averageDTO = new AverageDTO();
            averageDTO.setAverage(average/reviewDTOS.size());
            averageDTO.setNumberOfReview(reviewDTOS.size());
            return averageDTO;
        }
        return null;
    }

    @Override
    public String getTextReview(UUID reviewId) {
        return Objects.requireNonNull(reviewRepository.findById(reviewId).orElse(null)).getText();
    }

}
