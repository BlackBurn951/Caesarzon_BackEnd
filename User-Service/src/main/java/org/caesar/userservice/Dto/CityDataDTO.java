package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityDataDTO {
    private Long id;
    private String city;
    private String cap;
    private String region;
    private String province;

    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null) return false;

        if(this.getClass() != o.getClass()) return false;

        CityDataDTO c = (CityDataDTO) o;
        // qui facciamo il controllo che le due persone siano uguali
        return city.equals(c.getCity()) && cap.equals(c.getCap()) &&
                region.equals(c.getRegion()) && province.equals(c.getProvince());
    }
}
