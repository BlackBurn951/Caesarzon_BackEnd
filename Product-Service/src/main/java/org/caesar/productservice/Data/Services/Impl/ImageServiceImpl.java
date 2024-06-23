package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.AvailabilityRepository;
import org.caesar.productservice.Data.Dao.ImageRepository;
import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Services.ImageService;
import org.caesar.productservice.Dto.ImageDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;


    @Override
    public boolean addOrUpdateImage(UUID productID, byte[] image) {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImage(image);
        imageDTO.setProductID(productID);

        try {
            System.out.println("Sono nel try per aggiungere l'immagine");
            Image myImage = modelMapper.map(imageDTO, Image.class);
            if(myImage != null) {
                imageRepository.save(myImage);
                log.debug("Caricamento immagine riuscito");
                return true;
            }else{
                log.debug("Immagine passata non trovata");
                return false;
            }

        }catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento dell'immagine");
            return false;
        }
    }

    @Override
    public ImageDTO getImage(UUID productID) {
        return modelMapper.map(imageRepository.findImageByidProduct(productID), ImageDTO.class);
    }



    @Override
    public boolean deleteImage(UUID productID) {
        try {
            imageRepository.deleteImageByidProduct(productID);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }
}
