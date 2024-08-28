package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ImageRepository;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ImageService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
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



    @Override
    public byte[] getImage(ProductDTO prod) {
        try{
            Image img= imageRepository.findByProduct(modelMapper.map(prod, Product.class));

            System.out.println("Immagine: "+ img.getId());
            if(img==null)
                return null;

            return img.getFile();
        } catch (Exception | Error e) {
            return null;
        }
    }

    @Override
    public ImageDTO findImage(ProductDTO product){
        try{
            Image img= imageRepository.findByProduct(modelMapper.map(product, Product.class));

            if(img==null)
                return null;

            return modelMapper.map(img, ImageDTO.class);
        } catch (Exception | Error e) {
            return null;
        }
    }

    @Override
    public boolean saveImage(ImageDTO image) {
        try{
            imageRepository.save(modelMapper.map(image, Image.class));

            return true;
        } catch (Exception | Error e) {
            return false;
        }
    }

    @Override
    public boolean deleteImage(ProductDTO product) {
        try{
            imageRepository.deleteByProduct(modelMapper.map(product, Product.class));

            return true;
        } catch (Exception | Error e) {
            return false;
        }
    }
}
