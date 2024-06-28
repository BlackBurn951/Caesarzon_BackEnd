package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.util.UnicodeUtil;

import java.util.UUID;

@Getter
@Setter
public class AvailabilityDTO {

    private int amount;
    private String size;
    private ProductDTO product;
    private UUID id;
}
