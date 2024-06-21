package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ReviewRepository;
import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ReviewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;

    @Override
    public UUID addOrUpdateReview(ReviewDTO reviewDTO, Product product) {

        if(reviewDTO == null){
            return null;
        }
        try {

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
    public Review getReview(String username) {
        return reviewRepository.findByuserID(username);
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
