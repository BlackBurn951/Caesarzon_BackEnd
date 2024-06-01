package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private Long id;
    private String roadName;
    private String houseNumber;
    private String roadType;

    private CityDataDTO city;

}
