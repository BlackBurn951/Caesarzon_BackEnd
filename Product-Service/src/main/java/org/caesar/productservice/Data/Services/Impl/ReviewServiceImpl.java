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
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ReviewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;
    private final static String REVIEW_SERVICE = "reviewService";

    public String fallbackCircuitBreaker(CallNotPermittedException e) {
        log.debug("Circuit breaker su reviewService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }


    @Override
//    @CircuitBreaker(name= REVIEW_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= REVIEW_SERVICE)
    public String addReview(ReviewDTO reviewDTO, ProductDTO productDTO) {
        try {
            Review review = reviewRepository.findById(reviewDTO.getId()).orElse(null);

            if(review == null)  //Controllo per l'update
                review = new Review();

            if(reviewDTO.getId()==null && reviewRepository.findReviewByUsernameAndProduct(reviewDTO.getUsername(),modelMapper.map(productDTO, Product.class))!=null)
                return "Limite recensioni raggiunto...";

            review.setProduct(modelMapper.map(productDTO, Product.class));
            review.setDate(LocalDate.now());

            if(checkText(reviewDTO.getText()) && checkEvaluation(reviewDTO.getEvaluation())) {  //Convalida dei dati in arrivo
                review.setText(reviewDTO.getText());
                review.setEvaluation(reviewDTO.getEvaluation());
            }
            else
                return "Problemi nell'aggiunta della recensione...";

            review.setUsername(reviewDTO.getUsername());

            reviewRepository.save(review);

            return "Recensione aggiunta con successo!";
        } catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento della recensione");
            return "Problemi nell'aggiunta della recensione...";
        }
    }

    @Override
//    @Retry(name= REVIEW_SERVICE)
    public Review getReviewById(UUID reviewID) {
        return reviewRepository.findById(reviewID).orElse(null);
    }

    @Override
    public ReviewDTO getReviewByUsernameAndProduct(String username, ProductDTO productDTO) {
        try {
            Review review= reviewRepository.findReviewByUsernameAndProduct(username, modelMapper.map(productDTO, Product.class));

            if(review==null)
                return null;

            return modelMapper.map(review, ReviewDTO.class);
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return null;
        }
    }


    @Override
    //    @Retry(name= REVIEW_SERVICE)
    public List<ReviewDTO> getReviewsByProduct(ProductDTO productDTO, int str) {
        return reviewRepository.findAllByproduct(modelMapper.map(productDTO, Product.class), PageRequest.of(str, 10)).stream()
                .map(a -> modelMapper.map(a, ReviewDTO.class))
                .toList();
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
    public AverageDTO getReviewAverage(ProductDTO productDTO) {
        List<Review> reviewDTOS = reviewRepository.findByproduct(modelMapper.map(productDTO, Product.class));

        if(reviewDTOS==null || reviewDTOS.isEmpty())
            return null;

        double average = 0;
        for (Review review : reviewDTOS) {
            average += review.getEvaluation();
        }
        AverageDTO averageDTO = new AverageDTO();
        averageDTO.setAverage(average / reviewDTOS.size());
        averageDTO.setNumberOfReview(reviewDTOS.size());
        return averageDTO;

    }

    @Override
    public String getTextReview(UUID reviewId) {
        return Objects.requireNonNull(reviewRepository.findById(reviewId).orElse(null)).getText();
    }

    @Override
    public int getNumberOfReview(ProductDTO productDTO, int star) {
        List<Review> result= reviewRepository.findAllByProductAndEvaluation(modelMapper.map(productDTO, Product.class), star);

        return result==null || result.isEmpty()? 0: result.size();
    }


    //METODI DI SERVIZIO
    private boolean checkText(String text) {
        return !text.isEmpty() && text.length()<=256;
    }

    private boolean checkEvaluation(int evaluation) {
        return evaluation>=1 && evaluation<=5;
    }
}