package org.caesar.productservice.Dto.DTOOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayPalPurchaseDTO {
    private String paymentId;
    private String token;
    private String payerId;
    private BuyDTO buyDTO;
}
