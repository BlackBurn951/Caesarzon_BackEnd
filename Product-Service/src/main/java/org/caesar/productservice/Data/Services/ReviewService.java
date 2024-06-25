package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Dto.AverageDTO;
import org.caesar.productservice.Dto.ReviewDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewService {

    UUID addOrUpdateReview(ReviewDTO review);
    Review getReviewById(UUID reviewID);
    UUID getReviewIDByUsernameAndProductID(String username, UUID productID);
    List<ReviewDTO> getReviewsByProductId(UUID product);
    boolean deleteReview(UUID id);
    AverageDTO getProductAverage(UUID productID);

}
