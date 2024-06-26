package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.SingleWishListProductDTO;
import org.caesar.productservice.Dto.WishListProductDTO;
import org.caesar.productservice.Dto.WishlistDTO;

import java.util.List;
import java.util.UUID;

public interface WishlistProductService {

    boolean addOrUpdateWishlistProduct(WishListProductDTO wishlistProduct);
    boolean deleteAllProductsFromWishlist(WishlistDTO wishlistID);
    boolean deleteProductFromWishlist(WishListProductDTO wishListProductDTO);
    List<WishListProductDTO> getWishlistProductsByWishlistID(UUID wishListId);

}
