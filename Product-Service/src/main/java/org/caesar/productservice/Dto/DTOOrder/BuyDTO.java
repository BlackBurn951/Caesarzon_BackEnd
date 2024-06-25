package org.caesar.productservice.Dto.DTOOrder;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class BuyDTO {

    private UUID addressID;
    private UUID cardID;
    private List<UUID> productsIds;
}
