package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.caesar.productservice.Data.Entities.Product;
import org.springframework.web.multipart.MultipartFile;

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
    private String sport;





}
