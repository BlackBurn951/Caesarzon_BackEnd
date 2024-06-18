package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.SportProductService;
import org.caesar.productservice.Dto.ProductSportDTO;

import java.util.List;
import java.util.UUID;

public class SportProductServiceImpl implements SportProductService {
    @Override
    public boolean addSportProduct(ProductSportDTO sportProduct) {
        return false;
    }

    @Override
    public ProductSportDTO getSportProductById(UUID id) {
        return null;
    }

    @Override
    public List<ProductSportDTO> getAllSportProducts() {
        return List.of();
    }

    @Override
    public boolean deleteSportProduct(UUID id) {
        return false;
    }
}
