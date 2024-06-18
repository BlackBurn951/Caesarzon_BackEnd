package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.ProductOrderDTO;

import java.util.List;
import java.util.UUID;

public class ProductOrderServiceImpl implements ProductOrderService {
    @Override
    public UUID addOrUpdateProductOrder(ProductOrderDTO productOrder) {
        return null;
    }

    @Override
    public ProductOrderDTO getProductOrder(UUID id) {
        return null;
    }

    @Override
    public List<ProductOrderDTO> getProductOrders() {
        return List.of();
    }

    @Override
    public boolean deleteProductOrder(UUID id) {
        return false;
    }

    @Override
    public boolean deleteProductOrders(List<UUID> ids) {
        return false;
    }
}
