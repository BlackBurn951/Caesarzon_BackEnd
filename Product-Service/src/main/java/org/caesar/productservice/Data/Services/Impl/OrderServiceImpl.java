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
import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
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
        return orderRepository.findOrderByUsername(username).stream().map(a -> modelMapper.map(a, OrderDTO.class)).toList();
    }

    @Override
    public OrderDTO getOrder(String username, UUID id) {
        return modelMapper.map(orderRepository.findOrderByIdAndUsername(id, username), OrderDTO.class);
    }


}
