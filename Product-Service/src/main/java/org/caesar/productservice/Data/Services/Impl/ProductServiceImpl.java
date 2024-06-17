package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public class ProductServiceImpl implements ProductService{
    @Override
    public boolean addOrUpdateProduct(ProductDTO product) {
        return false;
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        return null;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return List.of();
    }

    @Override
    public boolean deleteProductById(UUID id) {
        return false;
    }
}
