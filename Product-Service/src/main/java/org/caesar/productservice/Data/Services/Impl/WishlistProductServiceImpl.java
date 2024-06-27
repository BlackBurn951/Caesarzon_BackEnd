package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.WishlistProductRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Entities.WishlistProduct;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.WishListProductDTO;
import org.caesar.productservice.Dto.WishlistDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
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
            WishlistProduct wishlistProductEntity = new WishlistProduct();

            wishlistProductEntity.setWishlistID(modelMapper.map(wishlistProduct.getWishlistID(), Wishlist.class));
            wishlistProductEntity.setProductID(modelMapper.map(wishlistProduct.getProductID(), Product.class));

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
                    modelMapper.map(wishListProductDTO.getProductID(), Product.class),
                    modelMapper.map(wishListProductDTO.getWishlistID(), Wishlist.class));
            return true;
        }catch(RuntimeException | Error e) {
            log.debug("Errore nella rimozione del prodotto dalla lista desideri");
            return false;
        }
    }



    @Override
    public List<WishListProductDTO> getWishlistProductsByWishlistID(WishlistDTO wishlistDTO){
        try {
            List<WishlistProduct> wishListProductDTOS = wishlistProductRepository.findAllByWishlistID(modelMapper.map(wishlistDTO, Wishlist.class));

            List<WishListProductDTO> wishListProductDTOS1 =  new Vector<>();

            WishListProductDTO wishListProductDTO;

            for(WishlistProduct wishlistProduct: wishListProductDTOS){
                wishListProductDTO = new WishListProductDTO();
                wishListProductDTO.setWishlistID(modelMapper.map(wishlistProduct.getWishlistID(), WishlistDTO.class));
                wishListProductDTO.setProductID(modelMapper.map(wishlistProduct.getProductID(), ProductDTO.class));
                wishListProductDTOS1.add(wishListProductDTO);
            }
            return wishListProductDTOS1;


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
