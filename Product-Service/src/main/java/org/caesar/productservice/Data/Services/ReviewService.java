package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Dto.AverageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    UUID addReview(ReviewDTO review, String username);
    Review getReviewById(UUID reviewID);
    UUID getReviewIDByUsernameAndProductID(String username, UUID productID);
    List<ReviewDTO> getReviewsByProductId(UUID product);
    boolean deleteReview(UUID id);
    AverageDTO getReviewAverage(UUID productID);
    String getTextReview(UUID reviewId);
    int getNumberOfReview(ProductDTO productDTO, int star);
}
