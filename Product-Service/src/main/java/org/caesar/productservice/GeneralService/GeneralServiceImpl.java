package org.caesar.productservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.AvailabilityRepository;
import org.caesar.productservice.Data.Dao.SportProductRepository;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.SportProductService;
import org.caesar.productservice.Data.Services.SportService;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.SendProductDTO;
import org.caesar.productservice.Dto.SportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GeneralServiceImpl implements GeneralService {

    private final ProductService productService;
    private final AvailabilityService availabilityService;
    private final ModelMapper modelMapper;
    private final SportProductService sportProductService;
    private final SportService sportService;

    @Override
    public boolean addProduct(SendProductDTO sendProductDTO) {
        System.out.println("sendProductDTO before mapping:");
        System.out.println("Name: " + sendProductDTO.getName());
        System.out.println("Description: " + sendProductDTO.getDescription());
        System.out.println("Brand: " + sendProductDTO.getBrand());
        System.out.println("Price: " + sendProductDTO.getPrice());
        System.out.println("Discount: " + sendProductDTO.getDiscount());
        System.out.println("PrimaryColor: " + sendProductDTO.getPrimaryColor());
        System.out.println("SecondaryColor: " + sendProductDTO.getSecondaryColor());
        System.out.println("Availabilities: " + sendProductDTO.getAvailabilities());
        System.out.println("ListaSport: " + sendProductDTO.getListaSport());

        // Mappa sendProductDTO a ProductDTO
        ProductDTO productDTO = modelMapper.map(sendProductDTO, ProductDTO.class);

        // Stampa dettagli di productDTO dopo la mappatura
        System.out.println("productDTO after mapping:");
        System.out.println("ID: " + productDTO.getId());
        System.out.println("Name: " + productDTO.getName());
        System.out.println("Description: " + productDTO.getDescription());
        System.out.println("Brand: " + productDTO.getBrand());
        System.out.println("Price: " + productDTO.getPrice());
        System.out.println("Discount: " + productDTO.getDiscount());
        System.out.println("PrimaryColor: " + productDTO.getPrimaryColor());
        System.out.println("SecondaryColor: " + productDTO.getSecondaryColor());

        // Aggiorna l'ID del productDTO dopo averlo salvato
        productDTO.setId(productService.addOrUpdateProduct(productDTO).getId());

        // Stampa dettagli di productDTO dopo aver aggiornato l'ID
        System.out.println("productDTO after updating ID:");
        System.out.println("ID: " + productDTO.getId());
        availabilityService.addOrUpdateAvailability(sendProductDTO.getAvailabilities(), productDTO);
        for(SportDTO sport: sportService.getAllSports()) {
            for(int i=0; i<sendProductDTO.getListaSport().size(); i++) {
                if(sendProductDTO.getListaSport().get(i).equals(sport.getName()))
                    sportProductService.addSportProduct(productDTO, sport);
            }
        }

        return true;

    }

    @Override
    public List<Availability> getAvailabilityByProductID(UUID productID) {
        List<Availability> availabilities = new ArrayList<>();
        System.out.println("Sono in getAvailabilityByProductID");
        for(Availability availability : availabilityService.getAll()) {
            if(availability.getProduct().getId().equals(productID)) {
                availabilities.add(availability);

            }
        }
        return availabilities;
    }

}
