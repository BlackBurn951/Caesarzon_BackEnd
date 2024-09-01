package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.BasicWishlistDTO;
import org.caesar.productservice.Dto.ChangeVisibilityDTO;
import org.caesar.productservice.Dto.WishListProductDTO;
import org.caesar.productservice.Dto.WishlistDTO;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    UUID addOrUpdateWishlist(WishlistDTO wishlist, String username);
    WishlistDTO getWishlist(UUID id, String username);
    List<BasicWishlistDTO> getAllWishlists(String ownerUsername, String accessUsername, int visibility);
    List<BasicWishlistDTO> getAllUserWishlists(String accessUsername);
    boolean deleteWishlist(UUID id);
    boolean changeVisibility(String username, ChangeVisibilityDTO changeVisibilityDTO);

    List<WishlistDTO> validateOrRollbackDeleteUserWishlist(String username, boolean rollback);
}
