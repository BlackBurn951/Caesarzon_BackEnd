package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistProductRepository extends JpaRepository<WishlistProduct, UUID> {
    void deleteByProductID(Product productID);
    void deleteByWishlistID(Wishlist wishlistID);

    void deleteWishlistProductByProductIDAndWishlistID(Product product, Wishlist wishlist);

    void deleteAllByWishlistID(Wishlist wishlist);

    List<WishlistProduct> findAllByWishlist(Wishlist wishlist);
}
