package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private Long id;

    private String street_name;
    private String house_number;
    private String street_type;

    private String city;
    private String cap;
    private String region;
    private String province;

}
