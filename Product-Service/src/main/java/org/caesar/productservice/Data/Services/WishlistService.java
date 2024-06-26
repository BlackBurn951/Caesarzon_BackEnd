package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Dto.WishlistDTO;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    UUID addOrUpdateWishlist(WishlistDTO wishlist, String username);
    WishlistDTO getWishlist(UUID id, String username);
    List<WishlistDTO> getAllWishlists(String userUsername, String visibility);
    boolean deleteWishlist(UUID id);
}
