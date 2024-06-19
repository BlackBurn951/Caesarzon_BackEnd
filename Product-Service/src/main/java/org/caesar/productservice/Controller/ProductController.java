package org.caesar.productservice.Controller;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.SendProductDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-api")
public class ProductController {

    private final ModelMapper modelMapper;
    private final ProductService productService;
    private final GeneralService generalService;

    @PostMapping("/product")
    public ResponseEntity<String> addAvailability(@RequestBody SendProductDTO sendProductDTO) {

        if(generalService.addProduct(sendProductDTO))
            return new ResponseEntity<>("Product aggiunto", HttpStatus.OK);
        else
            return new ResponseEntity<>("Prodotto non aggiunto", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/product")
    public ResponseEntity<SendProductDTO> getProduct(@RequestParam String name) {
        if(modelMapper.map(productService.getProductByName(name), SendProductDTO.class) != null)
            return new ResponseEntity<>(modelMapper.map(productService.getProductByName(name), SendProductDTO.class), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
}
