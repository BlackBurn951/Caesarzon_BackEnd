package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product-api")
public class OrderController {

    private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;
    private final ProductOrderService productOrderService;


    @PutMapping("/order")
    public ResponseEntity<String> createOrder(@RequestParam("product-id") UUID productId){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        if(productOrderService.updateOrder(username, productId))
            return new ResponseEntity<>("Ordine creato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella creazione dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/order")
    public ResponseEntity<String> createCart(@RequestBody SendProductOrderDTO sendProductOrderDTO ){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        if(generalService.createOrder(username, sendProductOrderDTO))
            return new ResponseEntity<>("Ordine creato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella creazione dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> updateCart(@RequestBody BuyDTO buyDTO){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        if(generalService.updateOrder(username, buyDTO))
            return new ResponseEntity<>("Ordine creato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella creazione dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
