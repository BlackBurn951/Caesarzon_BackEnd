package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;

import java.util.List;
import java.util.UUID;

public interface ProductOrderService {

    UUID addOrUpdateProductOrder(SendProductOrderDTO productOrder);

    SendProductOrderDTO getProductOrder(UUID id);

    List<SendProductOrderDTO> getProductOrders();

    boolean deleteProductCarts(String username);

    boolean save(ProductOrderDTO productOrderDTO);

    List<ProductOrderDTO> getProductOrdersByUsername(String username);

    boolean deleteProductCart(String username, ProductDTO productDTO);

    boolean saveAll(List<ProductOrderDTO> orderDTOS);

    boolean saveLater(String username, ProductDTO productDTO);

    boolean changeQuantity(String username, ProductDTO productDTO, int quantity, String size);

    List<ProductOrderDTO> getProductInOrder(String username, OrderDTO orderDTO);
}