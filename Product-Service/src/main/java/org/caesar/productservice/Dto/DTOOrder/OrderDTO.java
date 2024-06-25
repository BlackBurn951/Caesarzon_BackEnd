package org.caesar.productservice.Dto.DTOOrder;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class OrderDTO {

    private UUID id;
    private String orderNumber;
    private String orderState;
    private LocalDate expectedDeliveryDate;
    private LocalDate purchaseDate;
    private LocalDate refundDate;
    private boolean refund;
    private String refundState;
    private UUID addressID;
    private UUID cardID;
    private double totalOrder;
}
