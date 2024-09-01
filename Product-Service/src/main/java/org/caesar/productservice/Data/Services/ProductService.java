package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ProductSearchDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addOrUpdateProduct(ProductDTO product);

    ProductDTO getProductById(UUID id);

    boolean deleteProductById(UUID id);

    List<ProductDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);

    List<ProductDTO> getLastProducts();

    List<ProductDTO> getOffer();


}
