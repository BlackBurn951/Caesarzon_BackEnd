package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CityDataServiceimpl implements CityDataService {

    private final CityDataRepository cityDataRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<String> getCities(String sugg) {
        //Metodo per cercare le città che iniziano per sugg
        List<CityData> cities = cityDataRepository.findByCityIgnoreCaseStartingWith(sugg, PageRequest.of(0, 20));

        //Cast dell'oggetto entity nell'oggetto DTO
        List<CityDataDTO> citiesDTO= cities.stream().map(a -> modelMapper.map(a, CityDataDTO.class)).toList();

        return citiesDTO.stream().map(CityDataDTO::getCity).toList();
    }

    //Metodo per restituire i dati di una data città
    @Override
    public CityDataSuggestDTO getCityData(String city){
        return modelMapper.map(cityDataRepository.findByCity(city), CityDataSuggestDTO.class);
    }

}
