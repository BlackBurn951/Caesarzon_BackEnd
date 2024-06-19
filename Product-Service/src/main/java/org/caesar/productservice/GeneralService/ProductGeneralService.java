package org.caesar.productservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductGeneralService implements GeneralService {

    private final ProductService productService;
    private final AvailabilityService availabilityService;

    @Override
    public Product addAvailability(ProductDTO product) {
        System.out.println("Sono nell'add product del general service");
        List<Availability> availabilities = availabilityService.addOrUpdateAvailability(product.getAvailability());
        for (Availability availability : availabilities) {
            System.out.println("id: "+ availability.getId());
            System.out.println("quantità: "+ availability.getQuantity());
            System.out.println("size: "+ availability.getSize());
        }
        return productService.addOrUpdateProduct(product, availabilities) != null;
    }
}
