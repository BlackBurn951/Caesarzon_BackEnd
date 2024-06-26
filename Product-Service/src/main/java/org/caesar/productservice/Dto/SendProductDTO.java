package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.caesar.productservice.Data.Entities.Product;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class SendProductDTO {

    private UUID productId;
    private String name;
    private String description;
    private String brand;
    private double price;
    private int discount;
    private String primaryColor;
    private String secondaryColor;
    private Boolean is_clothing;
    private List<AvailabilityDTO> availabilities;
    private List<String> listaSport;

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
