package org.caesar.userservice.Data.Services;


import org.caesar.userservice.Dto.CityDataSuggestDTO;

import java.util.List;

public interface CityDataService {

    List<String> getCities(String sugg);

    CityDataSuggestDTO getCityData(String city);
}
