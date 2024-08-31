package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductCartDTO {
    private UUID id;
    private double total;
    private int quantity;
    private String name;
    private String size;
    private double discountTotal;
    private boolean buyLater;
}
