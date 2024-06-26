package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.WishlistProductRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Dto.SingleWishListProductDTO;
import org.caesar.productservice.Dto.WishListProductDTO;
import org.caesar.productservice.Dto.WishlistDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistProductServiceImpl implements WishlistProductService {

    private final ModelMapper modelMapper;
    private final WishlistProductRepository wishlistProductRepository;


    @Override
    public boolean addOrUpdateWishlistProduct(WishListProductDTO wishlistProduct) {
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
    public boolean deleteProductFromWishlist(WishListProductDTO wishListProductDTO){
        try{
            wishlistProductRepository.deleteWishlistProductByProductIDAndWishlistID(
                    modelMapper.map(wishListProductDTO.getProductDTO(), Product.class),
                    modelMapper.map(wishListProductDTO.getWishlistDTO(), Wishlist.class));
            return true;
        }catch(RuntimeException | Error e) {
            log.debug("Errore nella rimozione del prodotto dalla lista desideri");
            return false;
        }
    }

    @Override
    public List<WishListProductDTO> getWishlistProductsByWishlistID(UUID wishListId){
        try {
            return wishlistProductRepository.findWishlistProductById(wishListId).
                    stream().map(a -> modelMapper.map(a, WishListProductDTO.class)).toList();
        }catch(RuntimeException | Error e) {
            log.debug("Errore nella presa dei prodotti della lista");
            return null;
        }
    }




    @Override
    //Rimuove tutti i prodotti della lista desideri passando l'id della stessa
    public boolean deleteAllProductsFromWishlist(WishlistDTO wishlistDTO) {
        try {
            wishlistProductRepository.deleteAllByWishlistID(modelMapper.map(wishlistDTO, Wishlist.class));
            return true;
        }catch(RuntimeException | Error e) {
            log.debug("Errore nella rimozione dei prodotti dalla lista desideri");
            return false;
        }
    }
}
