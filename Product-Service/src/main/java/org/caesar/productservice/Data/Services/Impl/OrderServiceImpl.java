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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final Utils utils;

    //SimpleOrderService
    @Override
    public boolean addOrUpdateOrder(SimpleOrderDTO order) {
        return modelMapper.map(order, SimpleOrderDTO.class) != null;
    }

    @Override
    public SimpleOrderDTO getOrderById(UUID id) {

        return modelMapper.map(orderRepository.findById(id).orElse(null),
                SimpleOrderDTO.class);
    }

    @Override
    public List<SimpleOrderDTO> getAllSimpleOrders(UUID userID) {
        List<SimpleOrderDTO> orders = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            orders.add(modelMapper.map(order, SimpleOrderDTO.class));
        }
        return orders;
    }

    @Override
    public boolean deleteOrderById(UUID id) {
        for (Order order : orderRepository.findAll()) {
            if (order.getId().equals(id)) {
                orderRepository.delete(order);
                return true;
            }
        }
        return false;
    }

    //PurchaseOrderDTOService
    @Override
    public boolean addOrUpdateOrder(PurchaseOrderDTO order) {
        return modelMapper.map(order, PurchaseOrderDTO.class) != null;
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(UUID id) {
        Order purchaseOrder = orderRepository.findById(id).orElse(null);
        return modelMapper.map(purchaseOrder, PurchaseOrderDTO.class);
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return List.of();
    }

    @Override
    public boolean deletePurchaseOrderById(UUID id) {
        return false;
    }

    //ReturnOrderDTOService
    @Override
    public boolean addOrUpdateReturnOrder(ReturnOrderDTO order) {
        return false;
    }

    @Override
    public ReturnOrderDTO getReturnOrderById(UUID id) {
        return null;
    }

    @Override
    public List<ReturnOrderDTO> getAllReturnOrders() {
        return List.of();
    }

    @Override
    public boolean deleteReturnOrderById(UUID id) {
        return false;
    }

    @Override
    public OrderDTO addOrder(OrderDTO orderDTO) {
        try {
            return modelMapper.map(orderRepository.save(modelMapper.map(orderDTO, Order.class)), OrderDTO.class);

        } catch (Exception | Error e) {
            log.debug("Errore nella creazione dell'ordine");
            return null;
        }
    }

    @Override
    public List<OrderDTO> getOrders(String username) {
        return orderRepository.findAllOrdersByUsername(username).stream().map(a -> modelMapper.map(a, OrderDTO.class)).toList();
    }

    @Override
    public boolean updateOrder(String username) {
        try {
            LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
            Order order = orderRepository.findOrdersByUsername(username);
            if (order.getPurchaseDate().isBefore(tenDaysAgo)) {
                utils.sendNotify(username, "Reso ordine: "+order.getOrderNumber()+" rifiutato",
                        "Il reso è possibile solo entro 10 giorni dall'acquisto");
                return false;
            }else{
                order.setRefundDate(LocalDate.now());
                order.setOrderState("Rimborsato");
                order.setRefund(true);
                orderRepository.save(order);
                return utils.sendNotify(username, "Reso ordine: "+order.getOrderNumber()+" accettato",
                        "Il rimborso sarà effettuato sulla carta utilizzata al momento del pagamento");

            }
        }catch (Exception | Error e) {
            log.debug("Errore nell'update dell'ordine");
            return false;
        }
    }

    @Override
    public OrderDTO getOrder(String username, UUID id) {
        return modelMapper.map(orderRepository.findOrderByIdAndUsername(id, username), OrderDTO.class);
    }


}
