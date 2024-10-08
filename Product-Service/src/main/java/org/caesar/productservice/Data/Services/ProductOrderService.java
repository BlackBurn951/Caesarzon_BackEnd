package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Dto.WishlistDTO;

import java.util.List;

public interface ProductOrderService {

    boolean validateAndCompleteAndReleaseProductInOrder(List<ProductOrderDTO> products, boolean release);

    boolean rollbackProductInOrder(List<ProductOrderDTO> products);

    boolean deleteProductCarts(String username);

    boolean save(ProductOrderDTO productOrderDTO);

    List<ProductOrderDTO> getProductOrdersByUsername(String username);

    boolean deleteProductCart(String username, ProductDTO productDTO);

    boolean saveLater(String username, ProductDTO productDTO);

    boolean changeQuantity(String username, ProductDTO productDTO, int quantity, String size);

    List<ProductOrderDTO> getProductInOrder(String username, OrderDTO orderDTO);

    boolean validateOrRollbackDeleteUserCart(String username, boolean rollback);

    int checkIfBought(String username, ProductDTO productDTO);
}