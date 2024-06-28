package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SendProductOrderDTO {

    private UUID productID;
    private int quantity;
    private String size;

}
