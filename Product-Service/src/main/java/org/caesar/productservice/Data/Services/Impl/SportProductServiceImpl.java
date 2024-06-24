package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Dao.SportProductRepository;
import org.caesar.productservice.Data.Dao.SportRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Sport;
import org.caesar.productservice.Data.Entities.SportProduct;
import org.caesar.productservice.Data.Services.SportProductService;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ProductSportDTO;
import org.caesar.productservice.Dto.SportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SportProductServiceImpl implements SportProductService {

    private final ModelMapper modelMapper;
    private final SportProductRepository sportProductRepository;

    @Override
    public boolean addSportProduct(ProductDTO product, SportDTO sport) {
        SportProduct sportProduct = new SportProduct();
        sportProduct.setProductId(modelMapper.map(product, Product.class));
        sportProduct.setSportID(modelMapper.map(sport, Sport.class));
        sportProductRepository.save(sportProduct);
        return true;
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
