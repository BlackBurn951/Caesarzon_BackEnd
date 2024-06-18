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
    public UUID addOrUpdateReview(ReviewDTO reviewDTO) {

        if(reviewDTO.getId() == null){
            return null;
        }
        try {
            Review review = modelMapper.map(reviewDTO, Review.class);
            return reviewRepository.save(review).getId();

        }catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento della recensione");
            return null;
        }
    }

    @Override
    public ReviewDTO getReview(UUID id) {
        return modelMapper.map(reviewRepository.findById(id), ReviewDTO.class);
    }

    @Override
    public List<ReviewDTO> getReviewsByProductId(UUID productId) {
        Product product = modelMapper.map(productId, Product.class);
        return reviewRepository.findByProductID(product)
                .stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .toList();
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
