package org.caesar.productservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.caesar.productservice.Data.Dao.OrderRepository;
import org.caesar.productservice.Data.Entities.Order;
import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.DTOOrder.PurchaseOrderDTO;
import org.caesar.productservice.Dto.DTOOrder.ReturnOrderDTO;
import org.caesar.productservice.Dto.DTOOrder.SimpleOrderDTO;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final static String ORDER_SERVICE = "orderService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su orderService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }

    //Aggiunge o modifica un SimpleOrder
    @Override
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    public boolean addOrUpdateOrder(SimpleOrderDTO order) {
        return modelMapper.map(order, SimpleOrderDTO.class) != null;
    }

    @Override
    // Restituisce un SimpleOrder tramite l'id passato
//    @Retry(name= ORDER_SERVICE)
    public SimpleOrderDTO getOrderById(UUID id) {

        return modelMapper.map(orderRepository.findById(id).orElse(null),
                SimpleOrderDTO.class);
    }

    @Override
    // Restituisce tutti i SimpleOrders presenti nel db
//    @Retry(name= ORDER_SERVICE)
    public List<SimpleOrderDTO> getAllSimpleOrders(UUID userID) {
        List<SimpleOrderDTO> orders = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            orders.add(modelMapper.map(order, SimpleOrderDTO.class));
        }
        return orders;
    }

    @Override
    // Elimina l'ordine dal db tramite il suo id
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    public boolean deleteOrderById(UUID id) {
        for (Order order : orderRepository.findAll()) {
            if (order.getId().equals(id)) {
                orderRepository.delete(order);
                return true;
            }
        }
        return false;
    }

    // Aggiunge o modifica un PurchaseOrder
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    @Override
    public boolean addOrUpdateOrder(PurchaseOrderDTO order) {
        return modelMapper.map(order, PurchaseOrderDTO.class) != null;
    }

    @Override
    // Restituisce un PurchaseOrder tramite l'id passato
//    @Retry(name= ORDER_SERVICE)
    public PurchaseOrderDTO getPurchaseOrderById(UUID id) {
        Order purchaseOrder = orderRepository.findById(id).orElse(null);
        return modelMapper.map(purchaseOrder, PurchaseOrderDTO.class);
    }

    @Override
    // Restituisce tutti i purchaseOrder
//    @Retry(name= ORDER_SERVICE)
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        List<PurchaseOrderDTO> orders = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            orders.add(modelMapper.map(order, PurchaseOrderDTO.class));
        }
        return orders;
    }

    @Override
    // Elimina un PurchaseOrder tramite il suo id
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    public boolean deletePurchaseOrderById(UUID id) {
        return false;
    }

    //ReturnOrderDTOService
    @Override
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    public boolean addOrUpdateReturnOrder(ReturnOrderDTO order) {
        return false;
    }

    @Override
//    @Retry(name= ORDER_SERVICE)
    public ReturnOrderDTO getReturnOrderById(UUID id) {
        return null;
    }

    @Override
//    @Retry(name= ORDER_SERVICE)
    public List<ReturnOrderDTO> getAllReturnOrders() {
        return List.of();
    }

    @Override
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    public boolean deleteReturnOrderById(UUID id) {
        return false;
    }

    @Override
    // Aggiunge un ordine al db e lo restituisce
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    public OrderDTO addOrder(OrderDTO orderDTO) {
        try {
            return modelMapper.map(orderRepository.save(modelMapper.map(orderDTO, Order.class)), OrderDTO.class);

        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return null;
        }
    }

    @Override
    // Restituisce tutti gli ordini di un determinato utente
//    @Retry(name= ORDER_SERVICE)
    public List<OrderDTO> getOrders(String username) {
        return orderRepository.findAllOrdersByUsername(username).stream().map(a -> modelMapper.map(a, OrderDTO.class)).toList();
    }



    @Override
    // Restituisce un determinato ordine di un utente
//    @Retry(name= ORDER_SERVICE)
    public OrderDTO getOrder(String username, UUID id) {
        return modelMapper.map(orderRepository.findOrderByIdAndUsername(id, username), OrderDTO.class);
    }

    @Override
//    @Retry(name= ORDER_SERVICE)
    public List<OrderDTO> getOrdersByState(String state) {
        return orderRepository.findAllByOrderState(state).stream().map(a -> modelMapper.map(a, OrderDTO.class)).toList();
    }

    @Override
//    @Retry(name= ORDER_SERVICE)
    public OrderDTO getOrderByIdAndUsername(UUID orderId, String username) {
        return modelMapper.map(orderRepository.findOrderByIdAndUsername(orderId, username), OrderDTO.class);
    }

    @Override
//    @CircuitBreaker(name= ORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= ORDER_SERVICE)
    public OrderDTO save(OrderDTO orderDTO){
        return  modelMapper.map(orderRepository.save(modelMapper.map(orderDTO, Order.class)), OrderDTO.class);
    }


}
