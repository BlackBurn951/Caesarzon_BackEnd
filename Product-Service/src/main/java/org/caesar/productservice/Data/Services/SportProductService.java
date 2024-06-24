package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Sport;
import org.caesar.productservice.Data.Entities.SportProduct;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ProductSportDTO;
import org.caesar.productservice.Dto.SportDTO;

import java.util.List;
import java.util.UUID;

public interface SportProductService {

    boolean addSportProduct(ProductDTO product, SportDTO sport);
    ProductSportDTO getSportProductById(UUID id);
    List<ProductSportDTO> getAllSportProducts();
    boolean deleteSportProduct(UUID id)
            ;
}
