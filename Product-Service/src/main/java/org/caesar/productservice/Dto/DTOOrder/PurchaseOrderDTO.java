package org.caesar.productservice.Dto.DTOOrder;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
public class PurchaseOrderDTO {

    private UUID id;
    private Date purchaseDate;
    private Date deliveryDate;
}
