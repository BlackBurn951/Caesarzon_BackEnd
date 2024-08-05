package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByproduct(Product product);
    List<Review> findByproduct(Product product);
    Review findReviewByUsernameAndProduct(String username, Product product);
    List<Review> findAllByProductAndEvaluation(Product product, int evaluation);
}

