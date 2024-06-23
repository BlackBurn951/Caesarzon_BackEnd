package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.SendProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Product addOrUpdateProduct(ProductDTO sendProductDTO) {


        if(!checkDescription(sendProductDTO.getDescription()) || !checkDiscount(sendProductDTO.getDiscount())
        || !checkName(sendProductDTO.getName()) || !checkPrice(sendProductDTO.getPrice())
                || !checkPrimaryColor(sendProductDTO.getPrimaryColor()) || !checkSecondaryColor(sendProductDTO.getSecondaryColor())) {
            return null;
        }

        try{
            Product product = new Product();
            if(sendProductDTO.getId() != null && productRepository.findById(sendProductDTO.getId()).isPresent())
            {
                product.setId(sendProductDTO.getId());
                product.setDescription(sendProductDTO.getDescription());
                product.setDiscount(sendProductDTO.getDiscount());
                product.setName(sendProductDTO.getName());
                product.setBrand(sendProductDTO.getBrand());
                product.setIs_clothing(sendProductDTO.getIs_clothing());
                product.setPrice(sendProductDTO.getPrice());
                product.setPrimaryColor(sendProductDTO.getPrimaryColor());
                product.setSecondaryColor(sendProductDTO.getSecondaryColor());

            }else{
                product = modelMapper.map(sendProductDTO, Product.class);
            }
            return productRepository.save(product);

        }catch (RuntimeException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UUID getProductIDByName(String name) {
        System.out.println("Risultato: "+productRepository.findByName(name));
        Product productID = productRepository.findProductByName(name);
        if(productID != null)
            return productID.getId();
        else
            log.debug("Il prodotto che cerchi non esiste");
        return null;
    }

    @Override
    public List<SendProductDTO> getProductByPrice(double priceMin, double priceMax) {
        List<Product> products = productRepository.findAll();
        List<SendProductDTO> sendProductDTOs = new ArrayList<>();
        for(Product product : products){
            if(product.getPrice() >= priceMin && product.getPrice() <= priceMax){
                sendProductDTOs.add(modelMapper.map(product, SendProductDTO.class));
            }
        }
        return sendProductDTOs;
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<SendProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, SendProductDTO.class))
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
        if(description == null || description.isEmpty())
            log.debug("Descrizione non corretta");
        return !description.isEmpty() && description.length()<=500;
    }

    private boolean checkName(String name) {
        if(name == null || name.isEmpty())
            log.debug("Nome non salvato");
        return !name.isEmpty() && name.length()<=50;
    }

    private boolean checkDiscount(int discount) {
        if(discount < 0)
            log.debug("Discount non salvato");
        return discount >= 0;
    }

    private boolean checkPrice(Double price)
    {   if(price < 0)
        log.debug("Price non salvato");
        return price>0;
    }

    private boolean checkPrimaryColor(String color) {
        if(color == null || color.isEmpty())
            log.debug("coloreP non salvato");
        return !color.isEmpty() && color.length()<=50;
    }

    private boolean checkSecondaryColor(String color) {
        if(color == null || color.isEmpty())
            log.debug("coloreS non salvato");
        return !color.isEmpty() && color.length()<50;
    }

}
