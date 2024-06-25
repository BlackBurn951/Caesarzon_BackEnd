package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;

import java.util.UUID;

@Getter
@Setter
public class ProductOrderDTO {

    private UUID id;
    private OrderDTO orderID;

    private ProductDTO productDTO;
    private double total;
    private int quantity;
    private String username;
    private boolean buyLater;
}
