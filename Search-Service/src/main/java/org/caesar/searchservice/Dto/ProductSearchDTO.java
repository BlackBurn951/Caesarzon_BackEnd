package org.caesar.searchservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductSearchDTO {
    private UUID id;
    private String name;
    private String brand;
    private int discount;
    private double price;
}
