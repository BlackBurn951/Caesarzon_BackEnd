package org.caesar.productservice.Dto.DTOOrder;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
public class ReturnOrderDTO {

    private UUID id;
    private boolean _return;
    private String status;
    private Date returnDate;
}
