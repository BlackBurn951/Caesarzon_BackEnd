package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private Long id;
    private String streetName;
    private String houseNumber;
    private String streetType;

    private CityDataDTO city;

}
