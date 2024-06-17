package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public class ReviewServiceImpl implements ReviewService {
    @Override
    public UUID addOrUpdateReview(ReviewDTO review) {
        return null;
    }

    @Override
    public ReviewDTO getReview(UUID id) {
        return null;
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        return List.of();
    }

    @Override
    public List<ReviewDTO> getReviewsByProductId(UUID productId) {
        return List.of();
    }

    @Override
    public boolean deleteReview(UUID id) {
        return false;
    }
}
