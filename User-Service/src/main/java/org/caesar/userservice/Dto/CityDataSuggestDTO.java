package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityDataSuggestDTO {
    private Long id;
    private String cap;
    private String region;
    private String province;
}
