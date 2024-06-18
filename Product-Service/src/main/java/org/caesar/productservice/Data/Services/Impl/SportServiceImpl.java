package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.SportRepository;
import org.caesar.productservice.Data.Entities.Sport;
import org.caesar.productservice.Data.Services.SportService;
import org.caesar.productservice.Dto.SportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SportServiceImpl implements SportService {

    private final ModelMapper modelMapper;
    private final SportRepository sportRepository;

    @Override
    public UUID getSportID(SportDTO sportDTO) {

        if (sportDTO.getSportID() != null) {
            return null;
        }
        Sport sport = modelMapper.map(sportDTO, Sport.class);
        return sportRepository.save(sport).getId();
    }

    @Override
    public List<SportDTO> getSportDTObySportName(String sportName) {
        return sportRepository.findByName(sportName)
                .stream()
                .map(sport -> modelMapper.map(sport, SportDTO.class))
                .toList();
    }
}
