package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.LastView;
import org.caesar.productservice.Dto.LastViewDTO;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;

public interface LastViewService {

    boolean save(String username, ProductDTO productDTO);
    List<LastViewDTO> getAllViewed(String username);
}
