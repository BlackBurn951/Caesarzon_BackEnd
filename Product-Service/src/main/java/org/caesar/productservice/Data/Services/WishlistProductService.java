package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.caesar.productservice.Dto.WishlistProductDTO;

import java.util.List;
import java.util.UUID;

public interface WishlistProductService {

    boolean addOrUpdateWishlistProduct(WishlistProductDTO wishlistProduct);
    List<WishlistProductDTO> getWishlistProductsByWishlistID(UUID wishlistId);
    boolean deleteWishlistProductByProductId(UUID productID);
    boolean deleteAllWishlistProductsByWishlistID(UUID wishlistID);
}
