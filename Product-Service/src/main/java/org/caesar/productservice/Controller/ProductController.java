package org.caesar.productservice.Controller;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-api")
public class ProductController {

    private final ModelMapper modelMapper;
    private final ProductService productService;

    @PostMapping("/product")
    public ResponseEntity<String> addProduct(@RequestBody ProductDTO product) {
        if (product == null){
            return new ResponseEntity<>("Errore nell'inserimento del prodotto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        productService.addOrUpdateProduct(productDTO);
        return new ResponseEntity<>("Prodotto aggiunto correttamente", HttpStatus.OK);
    }

    @GetMapping("/product")
    public ProductDTO getProduct(@RequestParam UUID id) {
        return modelMapper.map(productService.getProductById(id), ProductDTO.class);
    }
}
