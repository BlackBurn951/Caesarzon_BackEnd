package org.caesar.productservice.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.WishlistDTO;
import org.caesar.productservice.Dto.WishlistProductDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product-api")
public class WishlistController {

    private final WishlistService wishlistService;
    private final WishlistProductService wishlistProductService;



    @PostMapping("/wishlist")
    public ResponseEntity<String> addWishList(@RequestBody WishlistDTO wishlistDTO){
        if(wishlistService.addOrUpdateWishlist(wishlistDTO) != null)
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/wishlistProduct")
    public ResponseEntity<String> addProductIntoToList(@RequestBody WishlistProductDTO wishlistDTO){
        if(wishlistProductService.addOrUpdateWishlistProduct(wishlistDTO))
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
