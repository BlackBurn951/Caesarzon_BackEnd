package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SportDTO {

    private UUID sportID;
    private String sportName;
}
