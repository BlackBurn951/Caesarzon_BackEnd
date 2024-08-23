package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Dto.AverageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    String addReview(ReviewDTO review, ProductDTO productDTO);
    Review getReviewById(UUID reviewID);
    ReviewDTO getReviewByUsernameAndProduct(String username, ProductDTO productDTO);
    List<ReviewDTO> getReviewsByProduct(ProductDTO productDTO, int str);
    boolean validateDeleteReviews(String username, boolean rollback);
    List<ReviewDTO> completeDeleteReviews(String username);
    AverageDTO getReviewAverage(ProductDTO productDTO);
    String getTextReview(UUID reviewId);
    int getNumberOfReview(ProductDTO productDTO, int star);
}
