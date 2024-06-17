package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.ImageService;
import org.caesar.productservice.Dto.ImageDTO;

import java.util.List;
import java.util.UUID;

public class ImageServiceImpl implements ImageService {

    @Override
    public UUID addOrUpdateImage(ImageDTO image) {
        return null;
    }

    @Override
    public ImageDTO getImage(UUID id) {
        return null;
    }

    @Override
    public List<ImageDTO> getAllImages() {
        return List.of();
    }

    @Override
    public boolean deleteImage(UUID id) {
        return false;
    }

    @Override
    public boolean deleteAllImages() {
        return false;
    }
}
