package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Sport;
import org.caesar.productservice.Dto.SportDTO;

import java.util.UUID;

public interface SportService {

    UUID getSportID(SportDTO sport);
    SportDTO getSportNameByID(UUID sportID);
}
