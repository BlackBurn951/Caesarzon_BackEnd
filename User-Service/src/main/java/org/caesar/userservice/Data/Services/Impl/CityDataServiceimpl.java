package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.CityDataRepository;
import org.caesar.userservice.Data.Entities.CityData;
import org.caesar.userservice.Data.Services.CityDataService;
import org.caesar.userservice.Dto.CityDataDTO;
import org.caesar.userservice.Dto.CityDataSuggestDTO;
import org.modelmapper.ModelMapper;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityDataServiceimpl implements CityDataService {

    private final CityDataRepository cityDataRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<String> getCities(String sugg) {
        List<CityData> cities = cityDataRepository.findByCityIgnoreCaseStartingWith(sugg, PageRequest.of(0, 20));
        List<CityDataDTO> citiesDTO= cities.stream().map(a -> modelMapper.map(a, CityDataDTO.class)).toList();

        return citiesDTO.stream().map(CityDataDTO::getCity).toList();
    }


    @Override
    public CityDataSuggestDTO getCityData(String city){
        return modelMapper.map(cityDataRepository.findByCity(city), CityDataSuggestDTO.class);
    }

}
