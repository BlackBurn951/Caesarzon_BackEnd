package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.CityDataRepository;
import org.caesar.userservice.Data.Entities.CityData;
import org.caesar.userservice.Data.Services.CityDataService;
import org.caesar.userservice.Dto.CityDataDTO;
import org.caesar.userservice.Dto.CityDataSuggestDTO;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CityDataServiceimpl implements CityDataService {

    private final CityDataRepository cityDataRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<String> getCities(String sugg) {
       List<CityData> cities = cityDataRepository.findByNomeComuneIgnoreCaseStartingWith(sugg);
       List<CityDataDTO> citiesDTO= cities.stream().map(a -> modelMapper.map(a, CityDataDTO.class)).toList();
       return citiesDTO.stream().map(CityDataDTO::getCity).toList();


    }

    @Override
    public CityDataSuggestDTO getCityData(String city){
        return modelMapper.map(cityDataRepository.findByNomeComune(city), CityDataSuggestDTO.class);
    }

}
