package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Dto.WishlistProductDTO;

import java.util.List;
import java.util.UUID;

public class WishlistProductServiceImpl implements WishlistProductService {
    @Override
    public boolean addOrUpdateWishlistProduct(WishlistProductDTO wishlistProduct) {
        return false;
    }

    @Override
    public WishlistProduct getWishlistProductById(UUID id) {
        return null;
    }

    @Override
    public List<WishlistProductDTO> getWishlistProducts() {
        return List.of();
    }

    @Override
    public boolean deleteWishlistProductById(UUID id) {
        return false;
    }
}
