package org.caesar.productservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ImageRepository;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ImageService;
import org.caesar.productservice.Dto.ImageDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    //fare modifica dell'immagine con eventuale eliminazione delle singole immagini
    @Override
    public boolean addOrUpdateImage(UUID productID, MultipartFile file) {

        if(file == null){
            return false;
        }

        try {
                Product product = productRepository.getReferenceById(productID);
                Image imageEntity = new Image();

                imageEntity.setFile(file.getBytes());
                imageEntity.setProduct(product);

                imageRepository.save(imageEntity);

            return true;

        }catch (RuntimeException | Error | IOException e) {
            log.debug("Errore nell'inserimento dell'immagine");
            return false;
        }
    }

    @Override
    public ImageDTO getImage(Product product) {

        Image image = imageRepository.findImageByProduct(product);
        if(image == null){
            return null;
        }
        return modelMapper.map(image, ImageDTO.class);
    }

    @Override
    public List<Image> getAllProductImages(Product product) {
        List<Image> productImages = new ArrayList<>();
        for(Image image : imageRepository.findAll())
            if(image.getProduct().equals(product))
                productImages.add(image);
        return productImages;
    }


    @Override
    @Transactional
    public boolean deleteImage(Product product) {
        try {
            List<Image> imagesToDelete = new ArrayList<>();
            for(Image image : imageRepository.findAll()){
                if(image.getProduct().equals(product)) {
                    imagesToDelete.add(image);

                }
            }
            if(!imagesToDelete.isEmpty()){
                imageRepository.deleteAll(imagesToDelete);
                return true;
            }else
                return false;

        } catch (Exception e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }
}
