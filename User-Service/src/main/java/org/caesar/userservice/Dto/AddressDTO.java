package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    String street_name;
    String house_number;
    String street_type;

    String city;
    String cap;
    String region;
    String province;

}
