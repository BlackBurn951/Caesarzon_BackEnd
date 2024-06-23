package org.caesar.searchservice.Data.Services;

import lombok.RequiredArgsConstructor;
import org.caesar.searchservice.Data.Dao.SearchRepository;
import org.caesar.searchservice.Dto.ProductSearchDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Override
    public List<ProductSearchDTO> searchProducts(String username, String searchText) {
        return List.of();
    }
}
