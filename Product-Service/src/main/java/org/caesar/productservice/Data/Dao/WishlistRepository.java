package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}
