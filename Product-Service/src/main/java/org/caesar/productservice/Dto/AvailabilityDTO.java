package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class AvailabilityDTO {

    private UUID id;
    private int amount;
    private String size;
}
