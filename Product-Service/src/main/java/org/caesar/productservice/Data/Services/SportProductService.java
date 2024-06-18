package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.SportProduct;
import org.caesar.productservice.Dto.ProductSportDTO;

import java.util.List;
import java.util.UUID;

public interface SportProductService {

    boolean addSportProduct(ProductSportDTO sportProduct);
    ProductSportDTO getSportProductById(UUID id);
    List<ProductSportDTO> getAllSportProducts();
    boolean deleteSportProduct(UUID id);
}
