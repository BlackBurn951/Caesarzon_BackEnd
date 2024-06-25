package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Dto.ReviewDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewService {

    UUID addOrUpdateReview(ReviewDTO review, Product product);
    Review getReviewById(Product product);
    List<ReviewDTO> getReviewsByProductId(Product product);
    boolean deleteReview(UUID id);
}
