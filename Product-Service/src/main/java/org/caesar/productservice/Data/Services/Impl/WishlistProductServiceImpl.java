package org.caesar.productservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    private final static String WISHLISTPRODUCT_SERVICE = "wishlistProductService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su wishlistProductService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }


    @Override
    @CircuitBreaker(name= WISHLISTPRODUCT_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=WISHLISTPRODUCT_SERVICE)
    public boolean addOrUpdateWishlistProduct(WishListProductDTO wishlistProduct) {
        try {
            WishlistProduct wishlistProductEntity = new WishlistProduct();

            wishlistProductEntity.setWishlist(modelMapper.map(wishlistProduct.getWishlistDTO(), Wishlist.class));
            wishlistProductEntity.setProduct(modelMapper.map(wishlistProduct.getProductDTO(), Product.class));

            wishlistProductRepository.save(wishlistProductEntity);
            return true;
        }
        catch (RuntimeException | Error e) {
            log.debug("Errore nell'aggiunta del prodotto nella lista desideri");
            return false;
        }
    }
    @Override
    @CircuitBreaker(name= WISHLISTPRODUCT_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=WISHLISTPRODUCT_SERVICE)
    public boolean deleteProductFromWishlist(WishListProductDTO wishListProductDTO){
        try{
            wishlistProductRepository.deleteWishlistProductByProductAndWishlist(
                    modelMapper.map(wishListProductDTO.getProductDTO(), Product.class),
                    modelMapper.map(wishListProductDTO.getWishlistDTO(), Wishlist.class));
            return true;
        }catch(RuntimeException | Error e) {
            log.debug("Errore nella rimozione del prodotto dalla lista desideri");
            return false;
        }
    }



    @Override
    @Retry(name=WISHLISTPRODUCT_SERVICE)
    public List<WishListProductDTO> getWishlistProductsByWishlistID(WishlistDTO wishlistDTO){
        try {
            List<WishlistProduct> wishListProductDTOS = wishlistProductRepository.findAllByWishlist(modelMapper.map(wishlistDTO, Wishlist.class));

            List<WishListProductDTO> wishListProductDTOS1 =  new Vector<>();

            WishListProductDTO wishListProductDTO;

            for(WishlistProduct wishlistProduct: wishListProductDTOS){
                wishListProductDTO = new WishListProductDTO();
                wishListProductDTO.setWishlistDTO(modelMapper.map(wishlistProduct.getWishlist(), WishlistDTO.class));
                wishListProductDTO.setProductDTO(modelMapper.map(wishlistProduct.getProduct(), ProductDTO.class));
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
    @CircuitBreaker(name= WISHLISTPRODUCT_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=WISHLISTPRODUCT_SERVICE)
    public boolean deleteAllProductsFromWishlist(WishlistDTO wishlistDTO) {
        try {
            wishlistProductRepository.deleteAllByWishlist(modelMapper.map(wishlistDTO, Wishlist.class));
            return true;
        }catch(RuntimeException | Error e) {
            log.debug("Errore nella rimozione dei prodotti dalla lista desideri");
            return false;
        }
    }
}
