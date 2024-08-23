package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.caesar.productservice.Data.Dao.OrderRepository;
import org.caesar.productservice.Data.Entities.Order;
import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.DTOOrder.PurchaseOrderDTO;
import org.caesar.productservice.Dto.DTOOrder.ReturnOrderDTO;
import org.caesar.productservice.Dto.DTOOrder.SimpleOrderDTO;
import org.caesar.productservice.Utils.Utils;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final Utils utils;


    //2PC per la creazione dell'ordine
    @Override
    public UUID validateOrderForCreate() {
        try {
            Order order= new Order();

            order.setOrderState("In validazione");
            UUID orderId= orderRepository.save(order).getId();

            return orderId;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return null;
        }
    }

    @Override
    public boolean completeOrderForCreate(OrderDTO orderDTO) {
        try {
            Order order= orderRepository.findById(orderDTO.getId()).orElse(null);

            if(order==null)
                return false;

            order.setOrderNumber(orderDTO.getOrderNumber());
            order.setExpectedDeliveryDate(orderDTO.getExpectedDeliveryDate());
            order.setPurchaseDate(orderDTO.getPurchaseDate());
            order.setRefundDate(orderDTO.getRefundDate());
            order.setRefund(orderDTO.isRefund());
            order.setAddressID(orderDTO.getAddressID());
            order.setCardID(orderDTO.getCardID());
            order.setOrderTotal(orderDTO.getOrderTotal());
            order.setUsername(orderDTO.getUsername());

            orderRepository.save(order);


            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }

    @Override
    public boolean releaseLockOrderForCreate(UUID orderId) {
        try {
            Order order= orderRepository.findById(orderId).orElse(null);

            if(order==null)
                return false;

            order.setOrderState("Ricevuto");

            orderRepository.save(order);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }

    @Override
    public boolean rollbackOrderForCreate(UUID orderId) {
        try {
            orderRepository.deleteById(orderId);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }


    //2PC per il reso dell'ordine
    @Override
    public boolean validateOrderForReturn(UUID orderId) {
        try {
            Order order= orderRepository.findById(orderId).orElse(null);

            if(order==null)
                return false;

            order.setOrderState("In validazione");
            orderRepository.save(order);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }

    @Override
    public boolean completeOrderForReturn(UUID orderId) {
        try {
            Order order= orderRepository.findById(orderId).orElse(null);

            if(order==null)
                return false;

            order.setRefundDate(LocalDate.now());
            order.setRefund(true);
            orderRepository.save(order);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }

    @Override
    public boolean releaseLockOrderForReturn(UUID orderId) {
        try {
            Order order= orderRepository.findById(orderId).orElse(null);

            if(order==null)
                return false;

            order.setOrderState("Rimborsato");
            orderRepository.save(order);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }

    @Override
    public boolean rollbackOrderForReturn(UUID orderId) {
        try {
            Order order= orderRepository.findById(orderId).orElse(null);

            if(order==null)
                return false;

            order.setOrderState("Ricevuto");
            order.setRefundDate(null);
            order.setRefund(false);
            orderRepository.save(order);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }


    //2PC per l'aggiornamento dell'ordine
    @Override
    public Map<UUID, List<String>> validateOrderForUpdate(boolean rollback) {
        try {
            List<Order> orders= orderRepository.findAllByOrderState("Ricevuto");

            if(orders==null)
                return null;

            Map<UUID, List<String>> result= new HashMap<>();
            List<String> numberAndUsername;

            for(Order order:orders) {
                order.setOrderState("In validazione");

                numberAndUsername= new Vector<>();
                numberAndUsername.add(order.getOrderNumber());
                numberAndUsername.add(order.getUsername());
                result.put(order.getId(), numberAndUsername);
            }

            orderRepository.saveAll(orders);

            return result;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return null;
        }
    }

    @Override
    public boolean completeOrderForUpdate(List<UUID> orderIds) {
        try {
            List<Order> orders= orderRepository.findAllById(orderIds);

            if(orders==null || orders.isEmpty())
                return false;

            for(Order order:orders) {
                order.setOrderState("In consegna");
            }

            orderRepository.saveAll(orders);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }

    @Override
    public boolean rollbackOrderForUpdate(List<UUID> orderIds) {
        try {
            List<Order> orders= orderRepository.findAllById(orderIds);

            if(orders==null || orders.isEmpty())
                return false;

            for(Order order:orders) {
                order.setOrderState("Ricevuto");
            }

            orderRepository.saveAll(orders);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return false;
        }
    }


    @Override  // Aggiunge un ordine al db e lo restituisce
    public OrderDTO addOrder(OrderDTO orderDTO) {
        try {
            return modelMapper.map(orderRepository.save(modelMapper.map(orderDTO, Order.class)), OrderDTO.class);

        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return null;
        }
    }

    @Override  // Restituisce tutti gli ordini di un determinato utente
    public List<OrderDTO> getOrders(String username) {
        return orderRepository.findAllOrdersByUsername(username).stream().map(a -> modelMapper.map(a, OrderDTO.class)).toList();
    }



    @Override  // Restituisce un determinato ordine di un utente
    public OrderDTO getOrder(String username, UUID id) {
        return modelMapper.map(orderRepository.findOrderByIdAndUsername(id, username), OrderDTO.class);
    }

    @Override
    public OrderDTO getOrderByIdAndUsername(UUID orderId, String username) {
        return modelMapper.map(orderRepository.findOrderByIdAndUsername(orderId, username), OrderDTO.class);
    }
}
