package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product-api")
public class WishlistController {

    private final WishlistService wishlistService;
           private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;


    @PostMapping("/wishlist") // Endpoint per l'aggiunta di una lista desideri
    public ResponseEntity<String> addWishList(@RequestBody WishlistDTO wishlistDTO){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        if(wishlistService.addOrUpdateWishlist(wishlistDTO, username) != null)
            return new ResponseEntity<>("Lista desideri creata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella creazione di una lista", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/wishlist/product")
    public ResponseEntity<String> addProductIntoList(@RequestBody SendWishlistProductDTO wishlistDTO){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.addProductIntoWishList(username, wishlistDTO))
            return new ResponseEntity<>("Prodotto aggiunto alla lista", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto alla lista", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/wishlist/{id}") // Endpoint per ottenere una lista desideri di un utente
    public ResponseEntity<WishlistDTO> getWishlist(@PathVariable UUID id){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        WishlistDTO wishlistDTO = wishlistService.getWishlist(id, username);
        if(wishlistDTO != null)
            return new ResponseEntity<>(wishlistDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/wishlist/products") // Endpoint per ottenere tutti i prodotti da una lista desideri di un utente
    public ResponseEntity<WishProductDTO> getWishlistProductsByWishlistID(@RequestParam("wish-id") UUID wishlistID){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        WishProductDTO wishProductDTO = generalService.getWishlistProductsByWishlistID(wishlistID, username);
        if(wishProductDTO != null)
            return new ResponseEntity<>(wishProductDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/wishlists") // Endpoint per ottenere tutte le liste desideri di una determinata visibilità di un utente
    public ResponseEntity<List<BasicWishlistDTO>> getUserWishlists(@RequestParam("usr") String ownerUsername, @RequestParam("visibility") int visibility){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        List<BasicWishlistDTO> result = wishlistService.getAllWishlists(ownerUsername, username, visibility);
        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/wishlists/all") // Endpoint per ottenere tutte le liste desideri di una determinata visibilità di un utente
    public ResponseEntity<List<BasicWishlistDTO>> getAllUserWishlists(){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        List<BasicWishlistDTO> result = wishlistService.getAllUserWishlists(username);
        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    @PutMapping("/wishlist/visibility")
    public ResponseEntity<String> getUserWishlists(@RequestBody ChangeVisibilityDTO changeVisibilityDTO){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        if(wishlistService.changeVisibility(username, changeVisibilityDTO))
            return new ResponseEntity<>("Visibilità cambiata con successo", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nel cambio della visibilità", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @DeleteMapping("/wishlist/{id}") // Endpoint per l'eliminazione di una lista desideri di un utente
    public ResponseEntity<String> deleteWishlist(@PathVariable UUID id){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        if(generalService.deleteWishlist(username, id))
            return new ResponseEntity<>("Lista desideri eliminata correttamente", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella cancellazione della lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @DeleteMapping("/wishlist/product")
    public ResponseEntity<String> deleteWishlistProductByProductID(@RequestParam("wish-id") UUID wishId, @RequestParam("product-id") UUID productId){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.deleteProductFromWishList(username, wishId, productId))
            return new ResponseEntity<>("Prodotto eliminato correttamente dalla lista desideri", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella cancellazione del prodotto dalla lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/wishlist/products")
    public ResponseEntity<String> deleteAllWishlistProductsByWishlistID(@RequestParam("wish-id") UUID wishlistID){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.deleteProductsFromWishList(username, wishlistID))
            return new ResponseEntity<>("Lista desideri svuotata", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nello svuotamento della lista desideri", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
