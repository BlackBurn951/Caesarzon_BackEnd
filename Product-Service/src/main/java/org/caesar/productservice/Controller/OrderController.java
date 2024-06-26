package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.ProductCartDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;
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
public class OrderController {

    private final GeneralService generalService;
    private final HttpServletRequest httpServletRequest;
    private final ProductOrderService productOrderService;
    private final OrderService orderService;


    //Metodi per la gestione del carrello
    @GetMapping("/cart")
    public ResponseEntity<List<ProductCartDTO>> getCart(){
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<ProductCartDTO> result= generalService.getCart(username);

        if(result==null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/cart") //Metodo per l'aggiunta del prodotto nel carrello
    public ResponseEntity<String> createCart(@RequestBody SendProductOrderDTO sendProductOrderDTO ){
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.createCart(username, sendProductOrderDTO))
            return new ResponseEntity<>("Ordine creato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella creazione dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/cart/product/{id}")  //Metodo per il salva più tardi e la modifica della quantità del singolo prodotto
    public ResponseEntity<String> changeCart(@PathVariable UUID id, @RequestParam(value= "quanity", required = false) int quantity, @RequestParam("action") int action) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= action==0? productOrderService.saveLater(username, id): productOrderService.changeQuantity(username, id, quantity);
        if(result)
            return new ResponseEntity<>("Ordine modificato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella modifica dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/cart/{id}") //Metodo per rimuovere il prodotto passato con l'id dal carrello
    public ResponseEntity<String> deleteProductInCart(@PathVariable UUID id){
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(productOrderService.deleteProductCart(username, id))
            return new ResponseEntity<>("Prodotto cancellato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nella cancellazione del prodotto...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/cart") //Metodo per svuotare il carrello da tutti i suoi prodotti
    public ResponseEntity<String> deleteProductsInCart(){
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(productOrderService.deleteProductCarts(username))
            return new ResponseEntity<>("Carello svuotato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Problemi nel svuotamento del carello...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/purchase")  //Metodo per effettuare l'acquisto del carrello
    public ResponseEntity<OrderDTO> makeOrder(@RequestBody BuyDTO buyDTO){
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        OrderDTO orderDTO = orderService.getOrder(username, );
        if(orderDTO != null)
            return new ResponseEntity<>(orderDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/purchases") // Metodo per ottenere tutti gli ordini di un utente
    public ResponseEntity<List<OrderDTO>> getOrders(){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        List<OrderDTO> orders = orderService.getOrders(username);
        if(!orders.isEmpty())
            return new ResponseEntity<>(orders, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/purchase/products")
    public ResponseEntity<List<ProductCartDTO>> getProductsInCart() {
        String username= httpServletRequest.getAttribute("preferred_username").toString();


    }

    @PostMapping("/purchase")  //Metodo per effettuare l'acquisto del carello
    public ResponseEntity<String> makeOrder(@RequestBody BuyDTO buyDTO){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        if(generalService.createOrder(username, buyDTO))
            return new ResponseEntity<>("Ordine creato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella creazione dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/purchase")  //Metodo per effettuare il reso
    public ResponseEntity<String> updateOrder(@RequestParam("order-id") UUID orderId) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        if(orderService.updateOrder(username))
            return new ResponseEntity<>("Ordine creato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella creazione dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
