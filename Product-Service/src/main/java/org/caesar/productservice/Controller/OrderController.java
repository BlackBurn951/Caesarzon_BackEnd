package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Data.Services.PayPalService;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.DTOOrder.*;
import org.caesar.productservice.Dto.ProductCartDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;
import org.caesar.productservice.Dto.RefundDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
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
    private final PayPalService payPalService;

    //Metodi per la gestione del carrello
    @GetMapping("/cart")
    public ResponseEntity<List<ProductCartDTO>> getCart(){
         String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<ProductCartDTO> result= generalService.getCart(username);

        if(result!=null)
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
    public ResponseEntity<String> changeCart(@PathVariable UUID id, @RequestParam(value= "quantity", required = false) Integer quantity, @RequestParam(value= "size", required = false) String size, @RequestParam("action") int action) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean result= action==0? generalService.saveLater(username, id): generalService.changeQuantity(username, id, Objects.requireNonNullElse(quantity, -1), size);
        if(result)
            return new ResponseEntity<>("Ordine modificato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nella modifica dell'ordine...", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @DeleteMapping("/cart/{id}") //Metodo per rimuovere il prodotto passato con l'id dal carrello
    public ResponseEntity<String> deleteProductInCart(@PathVariable UUID id){
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        if(generalService.deleteProductCart(username, id))
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



    @GetMapping("/orders") // Metodo per ottenere tutti gli ordini di un utente
    public ResponseEntity<List<OrderDTO>> getOrders(){
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<OrderDTO> orders = orderService.getOrders(username);
        if(!orders.isEmpty())
            return new ResponseEntity<>(orders, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/order/products/{id}")
    public ResponseEntity<List<ProductCartDTO>> getProductsInCart(@PathVariable UUID id) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<ProductCartDTO> result= generalService.getOrder(username, id);

        if(result!=null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }



    @PostMapping("/pre-order")  //Controllo ed eventuale messa da parte della disponibilità
    public ResponseEntity<List<UnavailableDTO>> checkAvailability(@RequestBody List<UUID> productIds) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        List<UnavailableDTO> result= generalService.checkAvailability(username, productIds);
        if(result!=null && result.getFirst()==null)
            return new ResponseEntity<>(null, HttpStatus.OK);
        else if(result==null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/purchase")  //Metodo per effettuare l'acquisto del carello
    public ResponseEntity<String> makeOrder(@RequestBody BuyDTO buyDTO, @RequestParam("pay-method") boolean payMethod){
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        
        String result= generalService.checkOrder(username, buyDTO, payMethod);
        if(result.equals("Errore"))
            return new ResponseEntity<>(result+"...", HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/success")
    public ResponseEntity<String> successPay(@RequestBody PayPalPurchaseDTO payPalPurchaseDTO) {

        if(!payPalService.executePayment(payPalPurchaseDTO.getPaymentId(), payPalPurchaseDTO.getPayerId()).getState().equals("approved"))
            return new ResponseEntity<>("Errore nel pagamento con paypal...", HttpStatus.INTERNAL_SERVER_ERROR);

        String username= httpServletRequest.getAttribute("preferred_username").toString();

        String result= generalService.createOrder(username, payPalPurchaseDTO.getBuyDTO());
        if(result.equals("Errore"))
            return new ResponseEntity<>(result+"...", HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/refund")  //Metodo per effettuare il reso
    public ResponseEntity<String> updateOrder(@RequestBody RefundDTO refundDTO) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();
        if(generalService.updateOrder(username, refundDTO.getPurchaseId()))
            return new ResponseEntity<>("Reso inviato con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nell'invio del reso'...", HttpStatus.INTERNAL_SERVER_ERROR);
	}

    @PutMapping("/orders/notify")
    public ResponseEntity<String> updateOrderNotify(){
        if(orderService.updateNotifyOrder())
            return new ResponseEntity<>("Notifiche aggiornate con successo!", HttpStatus.OK);
        else
            return new ResponseEntity<>("Errore nell'aggiornamento delle notifiche'...", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
