package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
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


    @PostMapping("/wishlist") // Endpoint per l'aggiunta di una lista desideri
    public ResponseEntity<String> addWishList(@RequestBody WishlistDTO wishlistDTO){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        if(wishlistService.addOrUpdateWishlist(wishlistDTO, username) != null)
            return new ResponseEntity<>("Lista desideri aggiunta", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nell'aggiunta di una lista", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/wishlist/product")
    public ResponseEntity<String> addProductIntoList(@RequestBody WishlistProductDTO wishlistDTO){
        if(wishlistProductService.addOrUpdateWishlistProduct(wishlistDTO))
            return new ResponseEntity<>("Prodotto aggiunto alla lista", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto alla lista", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/wishlist") // Endpoint per ottenere una lista desideri di un utente
    public ResponseEntity<WishlistDTO> getWishlist(UUID wishlistID){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        WishlistDTO wishlistDTO = wishlistService.getWishlist(wishlistID, username);
        if(wishlistDTO != null)
            return new ResponseEntity<>(wishlistDTO, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/wishlistProduct") // Endpoint per ottenere tutti i prodotti da una lista desideri di un utente
    public ResponseEntity<List<WishlistProductDTO>> getWishlistProductsByWishlistID(UUID wishlistID){
        List<WishlistProductDTO> allWishlistProducts;
        allWishlistProducts = wishlistProductService.getWishlistProductsByWishlistID(wishlistID);
        if(allWishlistProducts != null)
            return new ResponseEntity<>(allWishlistProducts, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/wishlists") // Endpoint per ottenere tutte le liste desideri di una determinata visibilità di un utente
    public ResponseEntity<List<WishlistDTO>> getUserWishlists(String username, String visibility){
        List<WishlistDTO> allUserWishlist = new ArrayList<>();
        wishlistService.getAllWishlists(username, visibility);
        return new ResponseEntity<>(allUserWishlist, HttpStatus.OK);
    }

    @DeleteMapping("/wishlist") // Endpoint per l'eliminazione di una lista desideri di un utente
    public ResponseEntity<String> deleteWishlist(@RequestParam UUID wishlistID){
        if(generalService.deleteWishlist(wishlistID))
            return new ResponseEntity<>("Lista desideri eliminata correttamente", HttpStatus.OK);
        else return new ResponseEntity<>("Errore nella cancellazione della lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @DeleteMapping("/wishlist/product/{productID}")
    public ResponseEntity<String> deleteWishlistProductByProductID(@PathVariable UUID productID){
        if(wishlistProductService.deleteWishlistProductByProductId(productID))
            return new ResponseEntity<>("Prodotto eliminato correttamente dalla lista desideri", HttpStatus.OK);
        else return new ResponseEntity<>("Errore nella cancellazione del prodotto dalla lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @DeleteMapping("/wishlist/products")
    public ResponseEntity<String> deleteAllWishlistProductsByWishlistID(@RequestParam UUID wishlistID){
        if(wishlistProductService.deleteAllWishlistProductsByWishlistID(wishlistID))
            return new ResponseEntity<>("Lista desideri svuotata", HttpStatus.OK);
        else return new ResponseEntity<>("Errore nello svuotamento della lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
