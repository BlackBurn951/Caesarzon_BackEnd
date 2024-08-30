package org.caesar.productservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Utils.CallCenter;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class OrderOrchestrator {

    private final OrderService orderService;
    private final ProductOrderService productOrderService;
    private final AvailabilityService availabilityService;
    private final CallCenter callCenter;

    public boolean processCreateOrderWithCardPayment(String username, List<ProductOrderDTO> productInOrder, double total, UUID addressId, UUID cardId) {

        //Fase di validazione in locale
        UUID orderId= orderService.validateOrderForCreate();
        boolean validateProduct= productOrderService.validateAndCompleteAndReleaseProductInOrder(productInOrder, false);

        if(orderId!=null && validateProduct){

            //Fase di validazione sui servizi esterni
            UUID notifyId= callCenter.validateNotification();
            boolean validatePayment= callCenter.validatePayment(cardId, total, false);

            if(notifyId!=null && validatePayment){


                //Fase di completamento in locale
                OrderDTO order= generateOrder(total, addressId, cardId, username);
                order.setId(orderId);

                boolean completeOrder= orderService.completeOrderForCreate(order),
                        completeProduct= false;

                for(ProductOrderDTO productOrderDTO: productInOrder){
                    productOrderDTO.setOrderDTO(order);
                    System.out.println(productOrderDTO.getTotal()+" "+order.getId());
                }

                completeProduct= productOrderService.validateAndCompleteAndReleaseProductInOrder(productInOrder, false);

                if(completeOrder && completeProduct){

                    //Fase di completamento sui servizi esterni
                    boolean completePayment= callCenter.completePayment(cardId, total),
                            completeNotify= callCenter.completeNotification(notifyId, username, "Ordine numero "+order.getOrderNumber()+" effettuato", "Il tuo ordine è in fase di elaborazione e sarà consegnato il "+ order.getExpectedDeliveryDate());

                    if(completePayment && completeNotify){

                        //Fase di rilascio dei lock su tutti i servizi
                        orderService.releaseLockOrderForCreate(orderId);
                        productOrderService.validateAndCompleteAndReleaseProductInOrder(productInOrder, true);
                        callCenter.releaseLockPayment(cardId);
                        callCenter.releaseNotification(notifyId);

                        return true;
                    }

                    //Fase di rollback post completamento su tutti i servizi
                    rollbackLocal(orderId, productInOrder);
                    rollbackRemote(cardId, total, false);

                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackLocal(orderId, productInOrder);
                rollbackRemote(cardId, total, true);

                return false;
            }

            //Fase di rollback pre completamento sui servizi esterni
            rollbackRemote(cardId, total, true);
        }

        //Fase di rollback pre completamento in locale
        rollbackLocal(orderId, productInOrder);

        return false;
    }

    public boolean processCreateOrderWithPaypalPayment(String username, List<ProductOrderDTO> productInOrder, double total, UUID addressId) {

        //Fase di validazione in locale
        UUID orderId= orderService.validateOrderForCreate();
        boolean validateProduct= productOrderService.validateAndCompleteAndReleaseProductInOrder(productInOrder, false);

        if(orderId!=null && validateProduct) {

            //Fase di validazione sui servizi esterni
            UUID notifyId = callCenter.validateNotification();

            if(notifyId!=null) {

                //Fase di completamento in locale
                OrderDTO order= generateOrder(total, addressId, null, username);
                order.setId(orderId);

                boolean completeOrder= orderService.completeOrderForCreate(order),
                        completeProduct= false;

                for(ProductOrderDTO productOrderDTO: productInOrder){
                    productOrderDTO.setOrderDTO(order);
                }

                completeProduct= productOrderService.validateAndCompleteAndReleaseProductInOrder(productInOrder, false);

                if(completeOrder && completeProduct){

                    //Fase di completamento sui servizi esterni
                    boolean completeNotify= callCenter.completeNotification(notifyId, username, "Ordine numero "+order.getOrderNumber()+" effettuato", "Il tuo ordine è in fase di elaborazione e sarà consegnato il "+ order.getExpectedDeliveryDate());

                    if(completeNotify){

                        //Fase di rilascio dei lock su tutti i servizi
                        orderService.releaseLockOrderForCreate(orderId);
                        productOrderService.validateAndCompleteAndReleaseProductInOrder(productInOrder, true);
                        callCenter.releaseNotification(notifyId);

                        return true;
                    }

                    //Fase di rollback post completamento su tutti i servizi
                    rollbackLocal(orderId, productInOrder);
                    rollbackRemote(null, total, false);

                    return false;
                }

                //Fase di rollback post completamento in locale
                rollbackLocal(orderId, productInOrder);
                rollbackRemote(null, total, true);

                return false;
            }

            //Fase di rollback pre completamento sui servizi esterni
            rollbackRemote(null, total, true);
        }

        //Fase di rollback pre completamento in locale
        rollbackLocal(orderId, productInOrder);

        return true;
    }

    public boolean processReturnOrder(String username, List<ProductOrderDTO> productInOrder, UUID orderId, List<AvailabilityDTO> availabilities, UUID cardId, double total, String orderNumber) {

        //Fase di validazione in locale
        boolean validateOrder= orderService.validateOrderForReturn(orderId),
                validateAvailabilities= availabilityService.validateAvailability(availabilities);

        if(validateOrder && validateAvailabilities) {

            //Fase di validazione sui servizi esterni
            UUID notifyId= callCenter.validateNotification();
            if(cardId!=null)
                return processReturnWithCard(username, productInOrder, orderId, availabilities, cardId, total, notifyId, orderNumber);
            else
                return processReturnWithoutCard(username, productInOrder, orderId, availabilities, notifyId, orderNumber);
        }

        //Fase di rollback pre completamento in locale
        rollbackLocalReturn(orderId, availabilities, true);

        return false;
    }

    public boolean processNotifyOrder() {

        //Fase di convalida in locale
        Map<UUID, List<String>> orders= orderService.validateOrderForUpdate(false);

        if(orders!=null) {

            //Fase di validazione sul servizio esterno
            UUID notifyId= callCenter.validateNotification();

            if(notifyId!=null) {

                //Fase di completamento sul servizio esterno
                boolean completeNotify= true;

                for(UUID orderId: orders.keySet()) {
                    if(!callCenter.completeNotification(notifyId, orders.get(orderId).get(1), "Aggiornamento ordine numero " + orders.get(orderId).get(0), "Il tuo ordine è in consegna e arriverà presto."))
                        completeNotify= false;
                }

                if(completeNotify){

                    //Fase di rilascio dei lock su tutti i servizi
                    orderService.completeOrderForUpdate(orders.keySet().stream().toList());
                    callCenter.releaseNotification(notifyId);

                    return true;
                }

                //Fase di rollback post completamento sul servizio esterno
                callCenter.rollbackNotification(notifyId);

                return false;
            }

            //Fase di rollback pre completamento sul servizio esterno
            callCenter.rollbackNotification(notifyId);
        }

        //Fase di rollback pre completamento in locale
        orderService.validateOrderForUpdate(true);

        return false;
    }

    //Metodi di servizio
    private OrderDTO generateOrder(double total, UUID addressId, UUID cardId, String username) {
        OrderDTO orderDTO= new OrderDTO();
        orderDTO.setOrderNumber(generaCodice(8));
        orderDTO.setOrderState("Ricevuto");
        orderDTO.setOrderTotal(total);
        orderDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
        orderDTO.setPurchaseDate(LocalDate.now());
        orderDTO.setRefund(false);
        orderDTO.setAddressID(addressId);
        orderDTO.setCardID(cardId);
        orderDTO.setUsername(username);

        return orderDTO;
    }
    private String generaCodice(int lunghezza) {
        String CHARACTERS = "5LDG8OKXCSV4EZ1YU9IR0HT7WMAJB2FN3P6Q";
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder codice = new StringBuilder(lunghezza);
        for (int i = 0; i < lunghezza; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            codice.append(CHARACTERS.charAt(index));
        }
        return codice.toString();
    }

    //Metodi di rollback per la creazione dell'ordine
    private void rollbackLocal(UUID orderId, List<ProductOrderDTO> productInOrder) {
        orderService.rollbackOrderForCreate(orderId);
        productOrderService.rollbackProductInOrder(productInOrder);
    }
    private void rollbackRemote(UUID cardId, double total, boolean preComplete) {
        if(cardId!=null) {
            if (preComplete)
                callCenter.validatePayment(cardId, total, true);
            else
                callCenter.rollbackPayment(cardId, total);
        }
        callCenter.rollbackNotification(cardId);
    }

    //Metodi di rollback per il reso
    private void rollbackLocalReturn(UUID orderId, List<AvailabilityDTO> availabilities, boolean validate) {
        orderService.rollbackOrderForReturn(orderId);
        availabilityService.rollbackAvailability(availabilities, validate);
    }
    private void rollbackRemoteReturn(UUID cardId, double total, boolean validate) {
        if(validate)
            callCenter.validateAndReleasePaymentForReturn(cardId, true);
        else
            callCenter.completeOrRollbackPaymentForReturn(cardId, total, true);
        callCenter.rollbackNotification(cardId);
    }


    //Gestione reso con pagamento con carta
    private boolean processReturnWithCard(String username, List<ProductOrderDTO> productInOrder, UUID orderId, List<AvailabilityDTO> availabilities, UUID cardId, double total, UUID notifyId, String orderNumber) {

        if (notifyId != null) {

            //Fase di completamento in locale
            boolean completeOrder = orderService.completeOrderForReturn(orderId);
            List<Integer> rollbackAvailability= new Vector<>();

            for (ProductOrderDTO productOrderDTO : productInOrder) {
                for(AvailabilityDTO availabilityDTO : availabilities) {
                    int amount= availabilityDTO.getAmount();
                    rollbackAvailability.add(amount);

                    availabilityDTO.setAmount(amount+productOrderDTO.getQuantity());
                }
            }

            boolean completeAvailability= availabilityService.completeAvailability(availabilities);

            if(completeOrder && completeAvailability) {

                //Fase completamento sui servizi esterni
                boolean completeCard= callCenter.completeOrRollbackPaymentForReturn(cardId, total, false),
                        completeNotify= callCenter.completeNotification(notifyId, username, "Reso ordine: "+orderNumber+" accettato", "Il rimborso sarà effettuato sul metodo di pagamento utilizzato al momento dell'acquisto");

                if(completeCard && completeNotify) {

                    //Fase di rilascio di tutti i lock
                    orderService.releaseLockOrderForReturn(orderId);
                    availabilityService.releaseLockAvailability(availabilities);
                    callCenter.validateAndReleasePaymentForReturn(cardId, true);
                    callCenter.releaseNotification(notifyId);

                    return true;
                }

                for(int i=0; i<availabilities.size(); i++) {
                    availabilities.get(i).setAmount(rollbackAvailability.get(i));
                }

                //Fase di rollback post completamento su i servizi esterni
                rollbackLocalReturn(orderId, availabilities, false);
                rollbackRemoteReturn(orderId, total, false);

                return false;
            }

            for(int i=0; i<availabilities.size(); i++) {
                availabilities.get(i).setAmount(rollbackAvailability.get(i));
            }

            //Fase di rollback post completamento in locale
            rollbackLocalReturn(orderId, availabilities, false);
            rollbackRemoteReturn(orderId, total, true);

            return false;
        }

        //Fase di rollback pre completamento sui servizi esterni
        rollbackLocalReturn(orderId, availabilities, true);
        rollbackRemoteReturn(orderId, total, true);

        return false;
    }

   private boolean processReturnWithoutCard(String username, List<ProductOrderDTO> productInOrder, UUID orderId, List<AvailabilityDTO> availabilities, UUID notifyId, String orderNumber) {

       if (notifyId != null) {

           //Fase di completamento in locale
           boolean completeOrder = orderService.completeOrderForReturn(orderId);
           List<Integer> rollbackAvailability= new Vector<>();

           for (ProductOrderDTO productOrderDTO : productInOrder) {
               for(AvailabilityDTO availabilityDTO : availabilities) {
                   int amount= availabilityDTO.getAmount();
                   rollbackAvailability.add(amount);

                   availabilityDTO.setAmount(amount+productOrderDTO.getQuantity());
               }
           }

           boolean completeAvailability= availabilityService.completeAvailability(availabilities);

           if(completeOrder && completeAvailability) {

               //Fase completamento sui servizi esterni
               boolean completeNotify= callCenter.completeNotification(notifyId, username, "Reso ordine: "+orderNumber+" accettato", "Il rimborso sarà effettuato sul metodo di pagamento utilizzato al momento dell'acquisto");

               if(completeNotify) {

                   //Fase di rilascio di tutti i lock
                   orderService.releaseLockOrderForReturn(orderId);
                   availabilityService.releaseLockAvailability(availabilities);
                   callCenter.releaseNotification(notifyId);

                   return true;
               }
           }

           for(int i=0; i<availabilities.size(); i++) {
               availabilities.get(i).setAmount(rollbackAvailability.get(i));
           }

           //Fase di rollback post completamento in locale
           rollbackLocalReturn(orderId, availabilities, false);
           callCenter.rollbackNotification(notifyId);

           return false;
       }

       //Fase di rollback pre completamento sui servizi esterni
       rollbackLocalReturn(orderId, availabilities, true);
       callCenter.rollbackNotification(notifyId);

       return false;
   }
}