package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Order;
import org.caesar.productservice.Dto.DTOOrder.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    //SimpleOrderDTO
    boolean addOrUpdateOrder(SimpleOrderDTO order);

    SimpleOrderDTO getOrderById(UUID id);

    List<SimpleOrderDTO> getAllSimpleOrders(UUID userID);

    boolean deleteOrderById(UUID id);

    //PurchaseOrderDTO
    boolean addOrUpdateOrder(PurchaseOrderDTO order);

    PurchaseOrderDTO getPurchaseOrderById(UUID id);

    List<PurchaseOrderDTO> getAllPurchaseOrders();

    boolean deletePurchaseOrderById(UUID id);

    //ReturnOrderDTO
    boolean addOrUpdateReturnOrder(ReturnOrderDTO order);

    ReturnOrderDTO getReturnOrderById(UUID id);

    List<ReturnOrderDTO> getAllReturnOrders();

    boolean deleteReturnOrderById(UUID id);

    OrderDTO addOrder(OrderDTO orderDTO);

    List<OrderDTO> getOrders(String username);


    OrderDTO getOrder(String username, UUID id);

    List<OrderDTO> getOrdersByState(String state);

    OrderDTO getOrderByIdAndUsername(UUID orderId, String username);

    OrderDTO save(OrderDTO orderDTO);


}
