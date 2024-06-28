package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.BasicWishlistDTO;
import org.caesar.productservice.Dto.WishlistDTO;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    UUID addOrUpdateWishlist(WishlistDTO wishlist, String username);
    WishlistDTO getWishlist(UUID id, String username);
    List<WishlistDTO> getAllWishlist(UUID id, String username);
    List<BasicWishlistDTO> getAllWishlists(String ownerUsername, String accessUsername, int visibility);
    boolean deleteWishlist(UUID id);
    boolean changeVisibility(int visibility, String username, UUID wishListId);
}
