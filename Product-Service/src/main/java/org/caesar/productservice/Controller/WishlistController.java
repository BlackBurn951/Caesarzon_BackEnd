package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.WishlistDTO;
import org.caesar.productservice.Dto.WishlistProductDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.caesar.productservice.GeneralService.GeneralServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product-api")
public class WishlistController {

    private final WishlistService wishlistService;
    private final WishlistProductService wishlistProductService;
    private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;


    @PostMapping("/wishlist")
    public ResponseEntity<String> addWishList(@RequestBody WishlistDTO wishlistDTO){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        if(wishlistService.addOrUpdateWishlist(wishlistDTO, username) != null)
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/wishlistProduct")
    public ResponseEntity<String> addProductIntoList(@RequestBody WishlistProductDTO wishlistDTO){
        if(wishlistProductService.addOrUpdateWishlistProduct(wishlistDTO))
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/wishlist")
    public ResponseEntity<WishlistDTO> getWishlist(UUID wishlistID){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        WishlistDTO wishlistDTO = wishlistService.getWishlist(wishlistID, username);
        if(wishlistDTO != null)
            return new ResponseEntity<>(wishlistDTO, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/wishlistProduct")
    public ResponseEntity<List<WishlistProductDTO>> getWishlistProductsByWishlistID(UUID wishlistID){
        List<WishlistProductDTO> allWishlistProducts;
        allWishlistProducts = wishlistProductService.getWishlistProductsByWishlistID(wishlistID);
        if(allWishlistProducts != null)
            return new ResponseEntity<>(allWishlistProducts, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/wishlists")
    public ResponseEntity<List<WishlistDTO>> getUserWishlists(String username, String visibility){
        List<WishlistDTO> allUserWishlist = new ArrayList<>();
        wishlistService.getAllWishlists(username, visibility);
        return new ResponseEntity<>(allUserWishlist, HttpStatus.OK);
    }

    @DeleteMapping("/wishlist") 
    public ResponseEntity<String> deleteWishlist(@RequestParam UUID wishlistID){
        if(generalService.deleteWishlist(wishlistID))
            return new ResponseEntity<>("Lista desideri eliminata correttamente", HttpStatus.OK);
        else return new ResponseEntity<>("Errore nella cancellazione della lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/wishlistProduct")
    public ResponseEntity<String> deleteWishlistProductByProductID(@RequestParam UUID productID){
        if(wishlistProductService.deleteWishlistProductByProductId(productID))
            return new ResponseEntity<>("Prodotto eliminato correttamente dalla lista desideri", HttpStatus.OK);
        else return new ResponseEntity<>("Errore nella cancellazione del prodotto dalla lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/wishlistProduct")
    public ResponseEntity<String> deleteAllWishlistProductsByWishlistID(@RequestParam UUID wishlistID){
        if(wishlistProductService.deleteAllWishlistProductsByWishlistID(wishlistID))
            return new ResponseEntity<>("Lista desideri svuotata", HttpStatus.OK);
        else return new ResponseEntity<>("Errore nello svuotamento della lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
