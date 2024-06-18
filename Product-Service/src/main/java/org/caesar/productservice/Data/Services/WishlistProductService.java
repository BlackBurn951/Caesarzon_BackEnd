package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.caesar.productservice.Dto.WishlistProductDTO;

import java.util.List;
import java.util.UUID;

public interface WishlistProductService {

    boolean addOrUpdateWishlistProduct(WishlistProductDTO wishlistProduct);
    WishlistProduct getWishlistProductById(UUID id);
    List<WishlistProductDTO> getWishlistProducts();
    boolean deleteWishlistProductById(UUID id);
}
