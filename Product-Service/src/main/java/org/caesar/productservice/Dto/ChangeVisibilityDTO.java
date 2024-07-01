package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangeVisibilityDTO {

    private UUID wishId;
    private int visibility;
}
