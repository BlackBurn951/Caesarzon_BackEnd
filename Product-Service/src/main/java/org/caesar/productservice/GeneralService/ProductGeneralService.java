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
import org.caesar.productservice.utils.ImageUtils;
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
        for(String sendImageDTO : sendProductDTO.getImages()){
            System.out.println("sendImageDTO: " + sendImageDTO);
        }
        if (product != null) {
            if(imageService.addOrUpdateImage(product, sendProductDTO.getImages()))
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

    @Override
    public List<String> getAllProductImages(UUID productID) {

        Product product = productService.getProductById(productID);
        List<String> images = new ArrayList<>();
        for(ImageDTO image: imageService.getAllProductImages(product)){
            images.add(new String(ImageUtils.convertByteArrayToBase64(image.getImage())));
        }
        return images;
    }


}
