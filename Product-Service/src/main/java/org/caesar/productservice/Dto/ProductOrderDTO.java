package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductOrderDTO {

    private UUID id;
    private UUID productId;
    private UUID orderId;
    private int quantity;
    private double totalPrice;
    private String userUsername;
}
