package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
