package org.caesar.productservice.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.AvailabilityDTO;
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

    @PostMapping("/product")
    public ResponseEntity<String> addProductAndAvailabilities(@RequestBody SendProductDTO sendProductDTO) {

        if(generalService.addProduct(sendProductDTO))
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/product")
    public ResponseEntity<SendProductDTO> getProductAndAvailabilities(@RequestParam String name) {
        UUID product = productService.getProductIDByName(name);
        if(product != null) {
            List<Availability> availabilities = generalService.getAvailabilityByProductID(product);
            List<AvailabilityDTO> availabilityDTOS = new ArrayList<>();
            for(Availability availability : availabilities) {
                availabilityDTOS.add(modelMapper.map(availability, AvailabilityDTO.class));
            }
            SendProductDTO finalProduct = new SendProductDTO(productService.getProductById(product), availabilityDTOS);

            return new ResponseEntity<>(finalProduct, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/price")
    public ResponseEntity<List<SendProductDTO>> getProductByPriceRange(@RequestParam double lower, @RequestParam double upper) {
        List<SendProductDTO> productDTOS = productService.getProductByPrice(lower, upper);
        if(productDTOS.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

}
