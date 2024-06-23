package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ImageRepository;
import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ImageService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.utils.ImageUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;


    @Override
    public boolean addOrUpdateImage(Product product, List<String> sendImagesDTO) {

        if(sendImagesDTO == null || sendImagesDTO.isEmpty()){
            log.debug("sendImagesDTO is null or empty");
            return false;
        }

        try {
            for(String sendImageDTO : sendImagesDTO) {
                byte[] imagebytes = ImageUtils.convertBase64ToByteArray(sendImageDTO);
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImage(imagebytes);
                imageDTO.setIdProduct(product);
                Image myImage = new Image();
                myImage.setFile(imageDTO.getImage());
                myImage.setIdProduct(product);
                imageRepository.save(myImage);

            }
            return true;

        }catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento dell'immagine");
            return false;
        }
    }

    @Override
    public ImageDTO getImage(Product product) {

        return modelMapper.map(imageRepository.findImageByidProduct(product), ImageDTO.class);
    }

    @Override
    public List<Image> getAllProductImages(Product product) {
        List<Image> productImages = new ArrayList<>();
        for(Image image : imageRepository.findAll())
            if(image.getIdProduct().equals(product))
                productImages.add(image);
        return productImages;
    }


    @Override
    public boolean deleteImage(Product product) {
        try {
            imageRepository.deleteImageByidProduct(product);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }
}
