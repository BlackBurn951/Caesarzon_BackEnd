package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;

import java.util.List;
import java.util.UUID;

public interface ProductOrderService {

    UUID addOrUpdateProductOrder(SendProductOrderDTO productOrder);

    SendProductOrderDTO getProductOrder(UUID id);

    List<SendProductOrderDTO> getProductOrders();

    boolean deleteProductCart(String username, UUID id);

    boolean deleteProductCarts(String username);

    boolean save(ProductOrderDTO productOrderDTO);

    List<ProductOrderDTO> getProductOrdersByUsername(String username);

    boolean saveAll(List<ProductOrderDTO> orderDTOS);

    boolean saveLater(String username, UUID productId);

    boolean changeQuantity(String username, UUID productId, int quantity);

}