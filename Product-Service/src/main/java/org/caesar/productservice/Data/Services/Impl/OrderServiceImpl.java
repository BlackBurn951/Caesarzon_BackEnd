package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
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
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    //SimpleOrderService
    @Override
    public boolean addOrUpdateOrder(SimpleOrderDTO order) {
        return false;
    }

    @Override
    public SimpleOrderDTO getOrderById(UUID id) {
        return null;
    }

    @Override
    public List<SimpleOrderDTO> getAllSimpleOrders() {
        return List.of();
    }

    @Override
    public boolean deleteOrderById(UUID id) {
        return false;
    }

    //PurchaseOrderDTOService
    @Override
    public boolean addOrUpdateOrder(PurchaseOrderDTO order) {
        return false;
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(UUID id) {
        return null;
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



}
