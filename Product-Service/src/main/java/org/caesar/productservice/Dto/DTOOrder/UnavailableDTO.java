package org.caesar.productservice.Dto.DTOOrder;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Dto.AvailabilityDTO;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UnavailableDTO {
    private UUID id;
    private String name;
    List<AvailabilityDTO> availabilities;
}
