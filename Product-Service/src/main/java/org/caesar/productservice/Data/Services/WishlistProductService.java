package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.WishListProductDTO;
import org.caesar.productservice.Dto.WishlistDTO;

import java.util.List;

public interface WishlistProductService {

    boolean addOrUpdateWishlistProduct(WishListProductDTO wishlistProduct);
    boolean deleteAllProductsFromWishlist(WishlistDTO wishlistID);
    boolean deleteProductFromWishlist(WishListProductDTO wishListProductDTO);
    List<WishListProductDTO> getWishlistProductsByWishlistID(WishlistDTO wishListId);
    boolean thereIsProductInWishList(WishlistDTO wishlistDTO, ProductDTO productDTO);

    int validateOrRollbackDeleteUserWish(List<WishlistDTO> wishlists, boolean rollback);
    List<WishListProductDTO> completeDeleteUserWish(List<WishlistDTO> wishlists);
    boolean releaseLockDeleteUserWish(List<WishlistDTO> wishlists);
}
