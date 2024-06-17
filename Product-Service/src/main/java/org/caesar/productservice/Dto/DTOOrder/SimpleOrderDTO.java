package org.caesar.productservice.Dto.DTOOrder;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class SimpleOrderDTO {

    private UUID id;
    private String order_num;

}
