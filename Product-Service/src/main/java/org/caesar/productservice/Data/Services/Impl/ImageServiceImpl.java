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
    public UUID addOrUpdateImage(ImageDTO imageDTO) {

        if(imageDTO.getId() == null){
            return null;
        }
        try {
            Image image = modelMapper.map(imageDTO, Image.class);
            return imageRepository.save(image).getId();

        }catch (RuntimeException | Error e) {
            log.debug("Errore nell'inserimento dell'immagine");
            return null;
        }
    }

    @Override
    public ImageDTO getImage(UUID id) {
        return modelMapper.map(imageRepository.findById(id), ImageDTO.class);
    }

    @Override
    public boolean deleteImage(UUID id) {
        try {
            imageRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }
}
