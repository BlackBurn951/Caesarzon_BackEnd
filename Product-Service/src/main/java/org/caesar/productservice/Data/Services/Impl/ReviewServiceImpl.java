package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Dao.ReviewRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Data.Services.ReviewService;
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
    public UUID addOrUpdateReview(ReviewDTO reviewDTO, Product product) {

        if(reviewDTO == null){
            return null;
        }
        try {
            System.out.println("Adding review");
            Review review = new Review();
            review.setProductID(product);
            review.setDate(LocalDate.now());
            review.setText(reviewDTO.getText());
            review.setEvaluation(reviewDTO.getEvaluation());
            review.setUserID(reviewDTO.getUserID());


            return reviewRepository.save(review).getId();

        }catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento della recensione");
            return null;
        }
    }

    @Override
    public Review getReviewById(UUID id) {
        return reviewRepository.findById(id).orElse(null);
    }


    @Override
    public Review getReview(String username, UUID productID) {
        return reviewRepository.findByuserIDAndProductID(username, productRepository.findById(productID).orElse(null));
    }

    @Override
    public List<ReviewDTO> getReviewsByProductId(Product product) {
        List<ReviewDTO> reviewDTOS = new ArrayList<>();
        for(Review review: reviewRepository.findByproductID(product)){
            ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
            System.out.println("text: "+review.getText());
            System.out.println("evaluation: "+review.getEvaluation());
            System.out.println("userID: "+review.getUserID());
            System.out.println("productID: "+review.getProductID());
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

}
