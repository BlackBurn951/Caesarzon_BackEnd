package org.caesar.productservice.Utils;

import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderSchedulerService {

    private final OrderService orderService;
    private final Utils utils;

    public OrderSchedulerService(OrderService orderService, Utils utils) {
        this.orderService = orderService;
        this.utils = utils;
    }

    @Scheduled(fixedRate = 30000) // Esegui ogni 30 secondi (30000 millisecondi)
    public void updateOrderStatuses() {
        // Recupera tutti gli ordini "Ricevuto" con data di consegna attesa entro oggi
        LocalDate today = LocalDate.now();
        List<OrderDTO> ordersToUpdate = orderService.getOrdersByStateAndDeliveryDate("Ricevuto", today);

        for (OrderDTO order : ordersToUpdate) {
            order.setOrderState("In consegna");
            orderService.addOrder(order); // aggiorna l'ordine nel database

            // Invia notifica all'utente
            utils.sendNotify(order.getUsername(),
                    "Aggiornamento ordine numero " + order.getOrderNumber(),
                    "Il tuo ordine è in consegna e arriverà presto."
            );
        }
    }
}
