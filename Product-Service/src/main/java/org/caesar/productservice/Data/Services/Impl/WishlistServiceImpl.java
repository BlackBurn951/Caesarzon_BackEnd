package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.WishlistDTO;

import java.util.List;
import java.util.UUID;

public class WishlistServiceImpl implements WishlistService {
    @Override
    public UUID addOrUpdateWishlist(WishlistDTO wishlist) {
        return null;
    }

    @Override
    public WishlistDTO getWishlist(UUID id) {
        return null;
    }

    @Override
    public List<WishlistDTO> getAllWishlists() {
        return List.of();
    }

    @Override
    public boolean deleteWishlist(UUID id) {
        return false;
    }

    @Override
    public boolean deleteWishlists(List<UUID> ids) {
        return false;
    }
}
