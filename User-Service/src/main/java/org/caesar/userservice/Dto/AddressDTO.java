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

    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null) return false;

        if(this.getClass() != o.getClass()) return false;

        AddressDTO a = (AddressDTO) o;

        return roadName.equals(a.getRoadName()) && houseNumber.equals(a.getHouseNumber()) &&
                roadType.equals(a.getRoadType()) && city.equals(a.getCity());
    }
}
