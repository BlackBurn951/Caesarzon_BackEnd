package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    List<Wishlist> findAllByUserUsername(String userUsername);

    Wishlist findWishlistByIdAndUserUsername(UUID id, String userUsername);

    List<Wishlist> findAllByUserUsernameAndVisibility(String userUsername, String visibility);

    List<Wishlist> findAllByIdAndUserUsername(UUID id, String userUsername);

}
