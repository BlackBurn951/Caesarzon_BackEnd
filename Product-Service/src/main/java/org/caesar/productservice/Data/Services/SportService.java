package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Sport;
import org.caesar.productservice.Dto.SportDTO;

import java.util.List;
import java.util.UUID;

public interface SportService {

    UUID getSportID(SportDTO sport);
    SportDTO getSportDTObySportName(String sportName);
    List<SportDTO> getAllSports();

}
