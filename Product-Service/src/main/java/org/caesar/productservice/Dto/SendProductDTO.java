package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
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
    private List<MultipartFile> images;
    private List<AvailabilityDTO> availabilities;

    public SendProductDTO() {}

    public SendProductDTO(Product product, List<AvailabilityDTO> availabilities, List<MultipartFile> images) {
        this.name = product.getName();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.price = product.getPrice();
        this.discount = product.getDiscount();
        this.primaryColor = product.getPrimaryColor();
        this.secondaryColor = product.getSecondaryColor();
        this.is_clothing = product.getIs_clothing();
        this.availabilities = availabilities;
        this.images = images;
    }


}
