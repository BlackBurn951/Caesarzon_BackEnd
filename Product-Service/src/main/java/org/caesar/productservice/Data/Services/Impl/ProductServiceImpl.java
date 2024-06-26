package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.ProductDTO;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.mapper.orm.Search;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    // Aggiunge il prodotto passato controllando se supera tutti i check dei parametri
    public Product addOrUpdateProduct(ProductDTO productDTO) {

        if(!checkDescription(productDTO.getDescription()) || !checkDiscount(productDTO.getDiscount())
        || !checkName(productDTO.getName()) || !checkPrice(productDTO.getPrice())
                || !checkPrimaryColor(productDTO.getPrimaryColor()) || !checkSecondaryColor(productDTO.getSecondaryColor())) {
            return null;
        }

        try{
            Product product = new Product();
            if(productDTO.getId() != null && productRepository.findById(productDTO.getId()).isPresent())
            {
                product.setId(productDTO.getId());
                product.setDescription(productDTO.getDescription());
                product.setDiscount(productDTO.getDiscount());
                product.setName(productDTO.getName());
                product.setBrand(productDTO.getBrand());
                product.setIs_clothing(productDTO.getIs_clothing());
                product.setPrice(productDTO.getPrice());
                product.setPrimaryColor(productDTO.getPrimaryColor());
                product.setSecondaryColor(productDTO.getSecondaryColor());
                product.setSport(productDTO.getSport());

            }else{
                product = modelMapper.map(productDTO, Product.class);
            }
            return productRepository.save(product);

        }catch (RuntimeException | Error e){
            log.debug(e.getMessage());
            return null;
        }
    }

    @Override
    // Restituisce l'id del prodotto partendo dal suo nome
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
    //Restituisce una lista di prodotti che rientrano nel range di prezzo selezionato
    public List<ProductDTO> getProductByPrice(double priceMin, double priceMax) {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> sendProductDTOs = new ArrayList<>();
        for(Product product : products){
            if(product.getPrice() >= priceMin && product.getPrice() <= priceMax){
                sendProductDTOs.add(modelMapper.map(product, ProductDTO.class));
            }
        }
        return sendProductDTOs;
    }

    @Override
    // Restituisce un prodotto partendo dal suo id
    public ProductDTO getProductById(UUID id) {
        return modelMapper.map(productRepository.findById(id), ProductDTO.class);
    }

    @Override
    // Restituisce tutti i prodotti
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    // Elimina il prodotto partendo dall'id specificato
    public boolean deleteProductById(UUID id) {
        try{
            productRepository.deleteById(id);
            return true;
        }catch (RuntimeException e) {
            log.debug("Errore nell'eliminazione del prodotto");
            return false;
        }
    }

    // Controllo della descrizione
    private boolean checkDescription(String description) {
        if(description == null || description.isEmpty())
            log.debug("Descrizione non corretta");
        assert description != null;
        return !description.isEmpty() && description.length()<=500;
    }

    // Controllo del nome
    private boolean checkName(String name) {
        if(name == null || name.isEmpty())
            log.debug("Nome non salvato");
        assert name != null;
        return !name.isEmpty() && name.length()<=50;
    }

    // Controllo dello sconto
    private boolean checkDiscount(int discount) {
        if(discount < 0)
            log.debug("Discount non salvato");
        return discount >= 0;
    }

    // Controllo del prezzo
    private boolean checkPrice(Double price)
    {   if(price < 0)
            log.debug("Price non salvato");
        return price>0;
    }

    // Controllo del colore primario
    private boolean checkPrimaryColor(String color) {
        if(color == null || color.isEmpty())
            log.debug("coloreP non salvato");
        assert color != null;
        return !color.isEmpty() && color.length()<=50;
    }

    // Controllo del colore secondario
    private boolean checkSecondaryColor(String color) {
        if(color == null || color.isEmpty())
            log.debug("coloreS non salvato");
        assert color != null;
        return !color.isEmpty() && color.length()<50;
    }

    // Effettua la ricerca dei prodotti seguendo i valori dei filtri passati per parametro
    public List<ProductDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing) {
        try {

            List<Product> results = Search.session(entityManager)
                    .search(Product.class)
                    .where(f -> {
                        BooleanPredicateClausesStep<?> bool = f.bool();

                        // Clausola per il testo di ricerca
                        bool.must(f.match()
                                .fields("name", "description", "brand", "primaryColor", "secondaryColor", "sport")
                                .matching(searchText)
                                .fuzzy(2));

                        // Clausola per il prezzo minimo e massimo
                        if (minPrice != null && maxPrice != null) {
                            bool.must(f.range()
                                    .field("price")
                                    .between(minPrice, maxPrice));
                        } else if (minPrice != null) {
                            bool.must(f.range()
                                    .field("price")
                                    .atLeast(minPrice));
                        } else if (maxPrice != null) {
                            bool.must(f.range()
                                    .field("price")
                                    .atMost(maxPrice));
                        }

                        // Clausola per isClothing
                        if (isClothing != null) {
                            bool.must(f.match()
                                    .field("is_clothing")
                                    .matching(isClothing));
                        }

                        return bool;
                    })
                    .fetchHits(20);


            return results.stream().map(a -> modelMapper.map(a, ProductDTO.class)).toList();


        } catch (Exception e) {
            log.error("Error while searching for products", e);
            return Collections.emptyList();
        }
    }




}
