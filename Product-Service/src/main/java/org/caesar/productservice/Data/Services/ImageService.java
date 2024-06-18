package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.ImageDTO;

import java.util.UUID;

public interface ImageService {

    UUID addOrUpdateImage(ImageDTO image);
    ImageDTO getImage(UUID id);
    boolean deleteImage(UUID id);
}
