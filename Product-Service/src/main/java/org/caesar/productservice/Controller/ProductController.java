package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.SearchService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ProductSearchDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product-api")
public class ProductController {

    private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;
    private final SearchService searchService;


    //Gestione del singolo prodotto
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductAndAvailabilitiesAndImages(@PathVariable UUID id) {
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        ProductDTO productDTO = generalService.getProductAndAvailabilitiesAndImages(username, id);
        if(productDTO!= null)
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/image/{productId}")
    public ResponseEntity<byte[]> getProductImages(@PathVariable UUID productId) {
        byte[] image = generalService.getProductImage(productId);

        if(image != null){
            return new ResponseEntity<>(image, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/image/{productId}")
    public ResponseEntity<String> putProductImages(@RequestParam("file") MultipartFile file, @PathVariable UUID productId) {

        if(generalService.saveImage(productId, file))
            return new ResponseEntity<>("Immagine caricata con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nel caricamento dell'immagine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/product") //Aggiunge il prodotto inviato con le sue disponibilità al db
    public ResponseEntity<UUID> addProductAndAvailabilities(@RequestBody ProductDTO sendProductDTO) {
        UUID prodId= generalService.addProduct(sendProductDTO);

        if(prodId!=null)
            return new ResponseEntity<>(prodId, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/product/{productID}")
    public ResponseEntity<String> deleteProductAndAvailabilities(@PathVariable UUID productID) {
        if (generalService.deleteProduct(productID))
            return new ResponseEntity<>("Prodotto eliminato", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non eliminato", HttpStatus.INTERNAL_SERVER_ERROR);
    }



    //RIcerca dei prodotti e caricamento homepage
    @GetMapping("/search") //Effettua una ricerca sul db e ritorna una lista di prodotti con i filtri selezionati
    public ResponseEntity<List<ProductSearchDTO>> searchProducts(
            @RequestParam("search-text") String query,
            @RequestParam(value = "min-price", required = false) Double minPrice,
            @RequestParam(value = "max-price", required = false) Double maxPrice,
            @RequestParam(value = "is-clothing", required = false) Boolean isClothing) {

        List<ProductSearchDTO> searchProduct = generalService.searchProducts(query, minPrice, maxPrice, isClothing);
        if(searchProduct.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(searchProduct, HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity<List<ProductSearchDTO>> newProduct(){
        List<ProductSearchDTO> searchProduct = generalService.getNewProducts();
        if(searchProduct.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(searchProduct, HttpStatus.OK);
    }

    @GetMapping("product/offer")
    public ResponseEntity<List<ProductSearchDTO>> getOffer(){
        List<ProductSearchDTO> searchProduct = generalService.getOffers();

        if(searchProduct==null || searchProduct.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(searchProduct, HttpStatus.OK);
    }

    @GetMapping("/lastSearchs") //Restituisce le ricerche dell'utente
    public ResponseEntity<List<String>> searches(){
        String username = httpServletRequest.getAttribute("preferred_username").toString();

        List<String> searches = searchService.getAllSearchs(username);
        if (searches != null)
            return new ResponseEntity<>(searches, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/lastView") //Restituisce i prodotti visti di recente dall'utente
    public ResponseEntity<List<ProductSearchDTO>> lastView(){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        List<ProductSearchDTO> searchProduct = generalService.getLastView(username);

        if(searchProduct.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(searchProduct, HttpStatus.OK);
    }

}
