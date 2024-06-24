package org.caesar.productservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.AvailabilityRepository;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Image;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
            for(int i = 0; i< sendProductDTO.getImages().size(); i++){
                if(!imageService.addOrUpdateImage(product,  sendProductDTO.getImages().get(i)))
                    return false;
            }
            return availabilityService.addOrUpdateAvailability(sendProductDTO.getAvailabilities(), product);
        }
        return false;

    }

    @Override
    public boolean deleteProduct(UUID id) {
        Product product = productService.getProductById(id);
        if(product != null){
            if(imageService.deleteImage(product))
            {

                if(availabilityService.deleteAvailabilityByProduct(product))
                    return productService.deleteProductById(id);
            }else
                return false;
        }
        return false;
    }

    @Override
    public List<ImageDTO> getProductImages(UUID id) {
        Product product = productService.getProductById(id);
        List<ImageDTO> images = new ArrayList<>();
        for(Image image: imageService.getAllProductImages(product)){
            if(image.getIdProduct().equals(product)){
                ImageDTO imageDTO = modelMapper.map(image, ImageDTO.class);
                images.add(imageDTO);
            }
        }
        return images;
    }


    @Override
    public List<Availability> getAvailabilityByProductID(UUID productID) {
        List<Availability> availabilities = new ArrayList<>();
        for(Availability availability : availabilityRepository.findAll()) {
            if(availability.getProduct().getId().equals(productID)) {
                availabilities.add(availability);

            }
        }
        return availabilities;
    }

    @Override
    public List<ImageDTO> getAllProductImages(UUID productID) {

       Product product = productService.getProductById(productID);
        List<ImageDTO> images = new ArrayList<>();
        for(Image image: imageService.getAllProductImages(product)){
            images.add(modelMapper.map(image, ImageDTO.class));
        }
        return images;
    }


}
