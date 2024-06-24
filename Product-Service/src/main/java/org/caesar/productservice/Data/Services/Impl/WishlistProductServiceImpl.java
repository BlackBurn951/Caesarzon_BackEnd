package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Dao.WishlistProductRepository;
import org.caesar.productservice.Data.Dao.WishlistRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Dto.WishlistProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistProductServiceImpl implements WishlistProductService {

    private final ModelMapper modelMapper;
    private final WishlistProductRepository wishlistProductRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    @Override
    public boolean addOrUpdateWishlistProduct(WishlistProductDTO wishlistProduct) {
        try {
            WishlistProduct wishlistProductEntity = modelMapper.map(wishlistProduct, WishlistProduct.class);
            wishlistProductRepository.save(wishlistProductEntity);
            return true;
        }
        catch (RuntimeException | Error e) {
            log.debug("Errore nell'aggiunta del prodotto nella lista desideri");
            return false;
        }
    }

    @Override
    public List<WishlistProductDTO> getWishlistProductsByWishlistID(UUID wishlistID) {
        List<WishlistProductDTO> wishlistProductDTOList = new ArrayList<>();
        for(WishlistProduct wishlistProduct: wishlistProductRepository.findAll()) {
            if(wishlistProduct.getWishlistID().getId().equals(wishlistID))
                wishlistProductDTOList.add(modelMapper.map(wishlistProduct, WishlistProductDTO.class));
        }
        return wishlistProductDTOList;
    }

    @Override
    public boolean deleteWishlistProductById(UUID productID) {

        Product product = productRepository.findById(productID).orElse(null);
        for(WishlistProduct wishlistProduct: wishlistProductRepository.findAll()) {
            if(wishlistProduct.getProductID().getId().equals(productID)) {
                wishlistProductRepository.deleteByProductID(product);
                return true;
            }
        }
        return false;
    }

    @Override
    //Rimuove tutti i prodotti della lista desideri passando l'id della stessa
    public boolean deleteAllWishlistProductsByWishlistID(UUID wishlistID) {
        Wishlist wishlist = wishlistRepository.getReferenceById(wishlistID);
        for(WishlistProduct wishlistProduct: wishlistProductRepository.findAll()) {
            if(wishlistProduct.getWishlistID().getId().equals(wishlistID)) {
                wishlistProductRepository.deleteByWishlistID(wishlist);
                return true;
            }
        }
        return false;
    }
}
