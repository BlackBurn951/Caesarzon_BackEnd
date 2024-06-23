package org.caesar.searchservice.Data.Services;

import org.caesar.searchservice.Dto.ProductSearchDTO;

import java.util.List;

public interface SearchService {
    List<ProductSearchDTO> searchProducts(String username, String searchText);
}
