package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistProductRepository extends JpaRepository<WishlistProduct, Long> {
}
