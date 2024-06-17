package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Dto.DTOOrder.PurchaseOrderDTO;
import org.caesar.productservice.Dto.DTOOrder.ReturnOrderDTO;
import org.caesar.productservice.Dto.DTOOrder.SimpleOrderDTO;

import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {

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
}
