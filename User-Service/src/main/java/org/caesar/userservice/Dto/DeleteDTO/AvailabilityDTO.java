package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AvailabilityDTO {

    private int amount;
    private String size;
    private ProductDTO product;
    private UUID id;
}
