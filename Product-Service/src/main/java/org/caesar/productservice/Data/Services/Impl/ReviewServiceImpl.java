package org.caesar.productservice.Data.Services.Impl;

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

    @Override
    public UUID addOrUpdateReview(ReviewDTO reviewDTO) {

        if(reviewDTO == null){
            return null;
        }
        try {
            Review review = new Review();
            System.out.println("Review creata");
            UUID productID = reviewDTO.getProductID();
            Product product = productRepository.findById(productID).orElse(null);
            if(product != null){
                System.out.println("Prodotto non nullo");
                review.setProduct(product);
                review.setDate(LocalDate.now());
                review.setText(reviewDTO.getText());
                review.setEvaluation(reviewDTO.getEvaluation());
                review.setUserID(reviewDTO.getUserID());

            }else{
                System.out.println("Prodotto nullo");
                return null;
            }

            System.out.println("Recensione sul prodotto salvata");
            return reviewRepository.save(review).getId();

        }catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento della recensione");
            return null;
        }
    }

    @Override
    public Review getReviewById(UUID reviewID) {

        return reviewRepository.findById(reviewID).orElse(null);
    }

    @Override
    public UUID getReviewIDByUsernameAndProductID(String username, UUID productID) {
        Product product = productRepository.findById(productID).orElse(null);
        return reviewRepository.findReviewByUserIDAndProduct(username, product).getId();
    }


    @Override
    public List<ReviewDTO> getReviewsByProductId(UUID productID) {
        Product product = productRepository.findById(productID).orElse(null);
        List<ReviewDTO> reviewDTOS = new ArrayList<>();
        for(Review review: reviewRepository.findByproduct(product)){
            ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
            System.out.println("text: "+review.getText());
            System.out.println("evaluation: "+review.getEvaluation());
            System.out.println("userID: "+review.getUserID());
            System.out.println("productID: "+review.getProduct());
            reviewDTOS.add(reviewDTO);
        }
        return reviewDTOS;
    }


    @Override
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
    public AverageDTO getProductAverage(UUID productID) {
        Product product = productRepository.findById(productID).orElse(null);
        if(product != null){
            List<Review> reviewDTOS = reviewRepository.findByproduct(product);
            double average = 0;
            for(Review review : reviewDTOS){
                average += review.getEvaluation();
            }
            AverageDTO averageDTO = new AverageDTO();
            averageDTO.setAverage(average/reviewDTOS.size());
            averageDTO.setNumberOfReviews(reviewDTOS.size());
            return averageDTO;
        }
        return null;
    }

}
