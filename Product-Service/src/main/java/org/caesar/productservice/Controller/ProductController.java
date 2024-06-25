package org.caesar.productservice.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.SendProductDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.modelmapper.ModelMapper;
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
public class ProductController {

    private final ModelMapper modelMapper;
    private final ProductService productService;
    private final GeneralService generalService;
    private final ProductRepository productRepository;
    private final ModelMapper model;

    @PostMapping("/product")
    public ResponseEntity<String> addProductAndAvailabilities(@RequestBody SendProductDTO sendProductDTO) {

        if(generalService.addProduct(sendProductDTO))
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/products")
    public ResponseEntity<String> addProductAndAvailabilities(@RequestBody List<SendProductDTO> sendProductDTO) {

        for(SendProductDTO sendProductDTO1: sendProductDTO){
            generalService.addProduct(sendProductDTO1);
        }
        return new ResponseEntity<>("Prodotti aggiunti correttamente", HttpStatus.OK);
    }

    @GetMapping("/product")
    public ResponseEntity<ProductDTO> getProductAndAvailabilitiesAndImages(@RequestParam String name) {
        UUID product = productService.getProductIDByName(name);
        if(product != null) {
            List<Availability> availabilities = generalService.getAvailabilityByProductID(product);
            List<ImageDTO> images = generalService.getAllProductImages(product);
            List<AvailabilityDTO> availabilityDTOS = new ArrayList<>();
            for(Availability availability : availabilities) {
                availabilityDTOS.add(modelMapper.map(availability, AvailabilityDTO.class));
            }

            ProductDTO finalProduct = new ProductDTO();
            // = new ProductDTO(productService.getProductById(product), availabilityDTOS, images);

            return new ResponseEntity<>(finalProduct, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/image")
    public ResponseEntity<List<ImageDTO>> getProductImages(@RequestParam UUID productID) {
        List<ImageDTO> images = generalService.getProductImages(productID);

        if(!images.isEmpty()){
            for(ImageDTO image : images){
                System.out.println(image.getFile());
            }
            return new ResponseEntity<>(images, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/price")
    public ResponseEntity<List<SendProductDTO>> getProductByPriceRange(@RequestParam double lower, @RequestParam double upper) {
        List<SendProductDTO> productDTOS = productService.getProductByPrice(lower, upper);
        if(productDTOS.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }



    @DeleteMapping("/product")
    public ResponseEntity<String> deleteProductAndAvailabilities(@RequestParam UUID productID) {
        if (generalService.deleteProduct(productID))
            return new ResponseEntity<>("Prodotto eliminato", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non eliminato", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam("search-text") String query,
            @RequestParam(value = "min-price", required = false) Double minPrice,
            @RequestParam(value = "max-price", required = false) Double maxPrice,
            @RequestParam(value = "is-clothing", required = false) Boolean isClothing) {
        System.out.println("min-price "+minPrice);
        System.out.println("max-price "+maxPrice);
        System.out.println("is-clothing "+isClothing);
        return productService.searchProducts(query, minPrice, maxPrice, isClothing);
    }

}
