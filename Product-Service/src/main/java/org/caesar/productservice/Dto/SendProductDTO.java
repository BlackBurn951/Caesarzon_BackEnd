package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SendProductDTO {

    private String name;
    private String description;
    private String brand;
    private double price;
    private int discount;
    private String primaryColor;
    private String secondaryColor;
    private Boolean is_clothing;
    private byte[] image;
    private List<AvailabilityDTO> availabilities;

    public SendProductDTO() {}

    public SendProductDTO(Product product, List<AvailabilityDTO> availabilities) {
        this.name = product.getName();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.price = product.getPrice();
        this.discount = product.getDiscount();
        this.primaryColor = product.getPrimaryColor();
        this.secondaryColor = product.getSecondaryColor();
        this.is_clothing = product.getIs_clothing();
        this.availabilities = availabilities;
    }
}
