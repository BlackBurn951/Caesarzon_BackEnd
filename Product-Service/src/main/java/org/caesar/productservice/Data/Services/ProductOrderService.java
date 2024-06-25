package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;

import java.util.List;
import java.util.UUID;

public interface ProductOrderService {

    UUID addOrUpdateProductOrder(SendProductOrderDTO productOrder);

    SendProductOrderDTO getProductOrder(UUID id);

    List<SendProductOrderDTO> getProductOrders();

    boolean deleteProductOrder(UUID id);

    boolean deleteProductOrders(List<UUID> ids);

    boolean save(ProductOrderDTO productOrderDTO);

    List<ProductOrderDTO> getProductOrdersByUsername(String username);

    boolean saveAll(List<ProductOrderDTO> orderDTOS);

    boolean updateOrder(String username, UUID productId);


}