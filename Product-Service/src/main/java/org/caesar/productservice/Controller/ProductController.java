package org.caesar.productservice.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-api")
public class ProductController {

    @RequestMapping("/value")
    public String prodotto() {
        return "Prodotto";
    }
}
