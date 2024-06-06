package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class AddressDTO {

    private UUID id;
    private String roadName;
    private String houseNumber;
    private String roadType;

    private CityDataDTO city;

}
