package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    UUID addOrUpdateReview(ReviewDTO review);
    ReviewDTO getReview(UUID id);
    List<ReviewDTO> getReviewsByProductId(UUID productId);
    boolean deleteReview(UUID id);
}
