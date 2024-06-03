package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddressDTO {

    private UUID id;
    private String roadName;
    private String houseNumber;
    private String roadType;

    private CityDataDTO city;

}
