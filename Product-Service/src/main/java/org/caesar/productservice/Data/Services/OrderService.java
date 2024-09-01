package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.DTOOrder.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {

    UUID validateOrderForCreate();

    boolean completeOrderForCreate(OrderDTO orderDTO);

    boolean releaseLockOrderForCreate(UUID orderId);

    boolean rollbackOrderForCreate(UUID orderId);


    boolean validateOrderForReturn(UUID orderId);

    boolean completeOrderForReturn(UUID orderId);

    boolean releaseLockOrderForReturn(UUID orderId);

    boolean rollbackOrderForReturn(UUID orderId);


    Map<UUID, List<String>> validateOrderForUpdate(boolean rollback);

    boolean completeOrderForUpdate(List<UUID> orderIds);


    List<OrderDTO> getOrders(String username);

    OrderDTO getOrder(String username, UUID id);

    OrderDTO getOrderByIdAndUsername(UUID orderId, String username);


    boolean validateDeleteUserOrders(String username, boolean rollback);
}
