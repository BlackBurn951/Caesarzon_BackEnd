package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.ImageDTO;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    UUID addOrUpdateImage(ImageDTO image);
    ImageDTO getImage(UUID id);
    List<ImageDTO> getAllImages();
    boolean deleteImage(UUID id);
    boolean deleteAllImages();
}
