package org.caesar.productservice.Data.Dao;

import jakarta.transaction.Transactional;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistProductRepository extends JpaRepository<WishlistProduct, UUID> {
    void deleteByProduct(Product productID);
    void deleteByWishlist(Wishlist wishlistID);

    @Transactional
    void deleteByProductAndWishlist(Product product, Wishlist wishlist);

    @Transactional
    void deleteAllByWishlist(Wishlist wishlist);

    List<WishlistProduct> findAllByWishlist(Wishlist wishlist);
    WishlistProduct findByProductAndWishlist(Product product, Wishlist wishlist);
}
