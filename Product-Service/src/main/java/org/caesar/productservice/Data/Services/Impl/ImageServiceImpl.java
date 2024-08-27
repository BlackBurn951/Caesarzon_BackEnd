package org.caesar.productservice.Data.Services.Impl;

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



    @Override
    public byte[] getImage(ProductDTO prod) {
        try{
            return imageRepository.findByProduct(modelMapper.map(prod, Product.class)).getFile();
        } catch (Exception | Error e) {
            return null;
        }
    }

    @Override
    public Image findImage(UUID productId){
        return imageRepository.findImageByProductId(productId);

    }

    @Override
    public boolean saveImage(Image image) {
        imageRepository.save(image);
        return true;
    }


}
