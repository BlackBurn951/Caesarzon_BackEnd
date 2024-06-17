package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.ProductOrder;
import org.caesar.productservice.Dto.ProductOrderDTO;

import java.util.List;
import java.util.UUID;

public interface ProductOrderService {

    UUID addOrUpdateProductOrder(ProductOrderDTO productOrder);
    ProductOrderDTO getProductOrder(UUID id);
    List<ProductOrderDTO> getProductOrders();
    boolean deleteProductOrder(UUID id);
    boolean deleteProductOrders(List<UUID> ids);
}
