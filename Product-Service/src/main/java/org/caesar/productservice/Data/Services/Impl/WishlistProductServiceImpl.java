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
            WishlistProduct wishlistProductEntity = new WishlistProduct();

            wishlistProductEntity.setId(wishlistProduct.getId());
            wishlistProductEntity.setWishlist(modelMapper.map(wishlistProduct.getWishlistDTO(), Wishlist.class));
            wishlistProductEntity.setProduct(modelMapper.map(wishlistProduct.getProductDTO(), Product.class));
            wishlistProductEntity.setOnDeleting(false);

            wishlistProductRepository.save(wishlistProductEntity);
            return true;
        }
        catch (Exception | Error e) {
            log.debug("Errore nell'aggiunta del prodotto nella lista desideri");
            return false;
        }
    }
    @Override
    public boolean deleteProductFromWishlist(WishListProductDTO wishListProductDTO){
        try{
            wishlistProductRepository.deleteWishlistProductByProductAndWishlist(
                    modelMapper.map(wishListProductDTO.getProductDTO(), Product.class),
                    modelMapper.map(wishListProductDTO.getWishlistDTO(), Wishlist.class));
            return true;
        }catch(Exception | Error e) {
            log.debug("Errore nella rimozione del prodotto dalla lista desideri");
            return false;
        }
    }



    @Override
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


        }catch(Exception | Error e) {
            log.debug("Errore nella presa dei prodotti della lista");
            return null;
        }
    }

    @Override
    public boolean thereIsProductInWishList(WishlistDTO wishlistDTO, ProductDTO productDTO) {
        Product product= modelMapper.map(productDTO, Product.class);
        Wishlist wishlist= modelMapper.map(wishlistDTO, Wishlist.class);

        WishlistProduct wishlistProduct= wishlistProductRepository.findByProductAndWishlist(product, wishlist);

        return wishlistProduct != null;
    }



    @Override
    public List<WishListProductDTO> validateOrRollbackDeleteUserWish(List<WishlistDTO> wishlists, boolean rollback) {
        try {
            List<WishlistProduct> wishListsProduct= new Vector<>();

            for (WishlistDTO wishlist: wishlists){
                List<WishlistProduct> wishListProductDTOS = wishlistProductRepository.findAllByWishlist(modelMapper.map(wishlist, Wishlist.class));

                if(wishListProductDTOS.isEmpty())
                    continue;

                wishListsProduct.addAll(wishListProductDTOS);
            }

            if(wishListsProduct.isEmpty())
                return new Vector<>();

            for(WishlistProduct wishlistProduct: wishListsProduct){
                wishlistProduct.setOnDeleting(!rollback);
            }

            wishlistProductRepository.saveAll(wishListsProduct);

            return wishListsProduct.stream()
                    .map(wh -> {
                        WishListProductDTO wish= new WishListProductDTO();
                        wish.setId(wh.getId());
                        wish.setWishlistDTO(modelMapper.map(wh.getWishlist(), WishlistDTO.class));
                        wish.setProductDTO(modelMapper.map(wh.getProduct(), ProductDTO.class));

                        return wish;
                    }).toList();
        }catch(Exception | Error e) {
            log.debug("Errore nella presa dei prodotti della lista");
            return null;
        }
    }

    @Override
    public boolean completeDeleteUserWish(List<WishlistDTO> wishlists) {
        try {
            List<WishlistProduct> wishListsProduct= new Vector<>();

            for (WishlistDTO wishlist: wishlists){
                List<WishlistProduct> wishListProductDTOS = wishlistProductRepository.findAllByWishlist(modelMapper.map(wishlist, Wishlist.class));

                if(wishListProductDTOS.isEmpty())
                    return false;

                wishListsProduct.addAll(wishListProductDTOS);
            }

            for(WishlistProduct wishlistProduct: wishListsProduct){
                wishlistProduct.setProduct(null);
            }

            wishlistProductRepository.saveAll(wishListsProduct);

            return true;
        }catch(Exception | Error e) {
            log.debug("Errore nella presa dei prodotti della lista");
            return false;
        }
    }

    @Override
    public boolean releaseLockDeleteUserWish(List<WishlistDTO> wishlists) {
        try {
            List<WishlistProduct> wishListsProduct= new Vector<>();

            for (WishlistDTO wishlist: wishlists){
                List<WishlistProduct> wishListProductDTOS = wishlistProductRepository.findAllByWishlist(modelMapper.map(wishlist, Wishlist.class));

                if(wishListProductDTOS.isEmpty())
                    return false;

                wishListsProduct.addAll(wishListProductDTOS);
            }

            for(WishlistProduct wishlistProduct: wishListsProduct){
                wishlistProductRepository.deleteAllByWishlist(wishlistProduct.getWishlist());
            }

            return true;
        }catch(Exception | Error e) {
            log.debug("Errore nella presa dei prodotti della lista");
            return false;
        }
    }


    @Override  //Rimuove tutti i prodotti della lista desideri passando l'id della stessa
    public boolean deleteAllProductsFromWishlist(WishlistDTO wishlistDTO) {
        try {
            wishlistProductRepository.deleteAllByWishlist(modelMapper.map(wishlistDTO, Wishlist.class));
            return true;
        }catch(Exception | Error e) {
            log.debug("Errore nella rimozione dei prodotti dalla lista desideri");
            return false;
        }
    }
}
