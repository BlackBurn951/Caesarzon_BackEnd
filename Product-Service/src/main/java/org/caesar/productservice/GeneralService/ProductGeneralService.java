package org.caesar.productservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.AvailabilityRepository;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.ImageService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.SendProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductGeneralService implements GeneralService {

    private final ProductService productService;
    private final AvailabilityService availabilityService;
    private final ModelMapper modelMapper;
    private final AvailabilityRepository availabilityRepository;
    private final ImageService imageService;


    @Override
    public boolean addProduct(SendProductDTO sendProductDTO) {

        ProductDTO productDTO = modelMapper.map(sendProductDTO, ProductDTO.class);
        Product product = productService.addOrUpdateProduct(productDTO);
        if (product != null) {
            if(imageService.addOrUpdateImage(product.getId(), sendProductDTO.getImage()))
                return availabilityService.addOrUpdateAvailability(sendProductDTO.getAvailabilities(), product);
            else
                return false;
        }
        return false;

    }


    @Override
    public List<Availability> getAvailabilityByProductID(UUID productID) {
        List<Availability> availabilities = new ArrayList<>();
        System.out.println("Sono in getAvailabilityByProductID");
        for(Availability availability : availabilityRepository.findAll()) {
            if(availability.getProduct().getId().equals(productID)) {
                availabilities.add(availability);

            }
        }
        return availabilities;
    }


}
