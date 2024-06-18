package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//Da capire se devo aggiungere la disponibilità alla lista dei prodotti
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Override
    public UUID addOrUpdateProduct(ProductDTO productDTO) {

        if(!checkDescription(productDTO.getDescription()) || !checkDiscount(productDTO.getDiscount())
        || !checkName(productDTO.getName()) || !checkPrice(productDTO.getPrice())
                || !checkPrimaryColor(productDTO.getPrimaryColor()) || !checkSecondaryColor(productDTO.getSecondaryColor()))
            return null;

        try{
            Product product = modelMapper.map(productDTO, Product.class);
            return productRepository.save(product).getId();

        }catch (RuntimeException e){
            log.error("Errore nellinserimento del prodotto");
            return null;
        }
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        return modelMapper.map(productRepository.findById(id), ProductDTO.class);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteProductById(UUID id) {
        try{
            productRepository.deleteById(id);
            return true;
        }catch (RuntimeException e) {
            log.debug("Errore nell'eliminazione del prodotto");
            return false;
        }
    }

    private boolean checkDescription(String description) {
        return !description.isEmpty() && description.length()<=500;
    }

    private boolean checkName(String name) {
        return !name.isEmpty() && name.length()<=50;
    }

    private boolean checkDiscount(int discount) {
        return discount >= 0;
    }

    private boolean checkPrice(Double price) {
        return price>0;
    }

    private boolean checkPrimaryColor(String color) {
        return !color.isEmpty() && color.length()<=50;
    }

    private boolean checkSecondaryColor(String color) {
        return !color.isEmpty() && color.length()<50;
    }
}
