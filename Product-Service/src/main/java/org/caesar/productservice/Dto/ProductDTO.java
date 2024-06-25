package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class ProductDTO {

    private UUID id;
    private String name;
    private String description;
    private String brand;
    private double price;
    private int discount;
    private String primaryColor;
    private String secondaryColor;
    private Boolean is_clothing;
    List<AvailabilityDTO> availabilities;
    private String sport;



}
