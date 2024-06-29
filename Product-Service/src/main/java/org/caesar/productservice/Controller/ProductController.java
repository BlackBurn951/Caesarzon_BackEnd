package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/product") //Aggiunge il prodotto inviato con le sue disponibilità al db
    public ResponseEntity<String> addProductAndAvailabilities(@RequestBody ProductDTO sendProductDTO) {

        if(generalService.addProduct(sendProductDTO))
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductAndAvailabilitiesAndImages(@PathVariable UUID id) {
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        ProductDTO productDTO = generalService.getProductAndAvailabilitiesAndImages(username, id);
        if(productDTO!= null){
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/product")
    public ResponseEntity<String> deleteProductAndAvailabilities(@RequestParam UUID productID) {
        if (generalService.deleteProduct(productID))
            return new ResponseEntity<>("Prodotto eliminato", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non eliminato", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/image")
    public ResponseEntity<List<ImageDTO>> getProductImages(@RequestParam UUID productID) {
        List<ImageDTO> images = generalService.getProductImages(productID);

        if(!images.isEmpty()){
            return new ResponseEntity<>(images, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


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
        List<ProductSearchDTO> searchProduct = generalService.newProducts();
        if(searchProduct.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(searchProduct, HttpStatus.OK);
    }

    @GetMapping("/offer")
    public ResponseEntity<List<ProductSearchDTO>> offer(){
        List<ProductSearchDTO> searchProduct = generalService.offer();
        if(searchProduct.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(searchProduct, HttpStatus.OK);
    }


    @GetMapping("/lastSearchs") //Restituisce le ricerche dell'utente
    public ResponseEntity<List<String>> searchs(){
        String username = httpServletRequest.getAttribute("preferred_username").toString();
        List<String> ricerche = searchService.getAllSearchs(username);
        if (ricerche != null)
            return new ResponseEntity<>(ricerche, HttpStatus.OK);
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
