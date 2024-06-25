package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Dao.LastViewRepository;
import org.caesar.productservice.Data.Entities.LastView;
import org.caesar.productservice.Data.Services.LastViewService;
import org.caesar.productservice.Dto.LastViewDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LastViewServiceImpl implements LastViewService {

    private final LastViewRepository lastViewRepository;
    private final ModelMapper modelMapper;

    @Override
    public boolean save(String username, ProductDTO productDTO) {
        try{
            LastViewDTO lastViewDTO = new LastViewDTO();

            lastViewDTO.setUsername(username);
            lastViewDTO.setProduct(productDTO);

            lastViewRepository.save(modelMapper.map(lastViewDTO, LastView.class));

            return true;
        }catch (Exception | Error e){
            System.out.println("Problema nell'inserimento delle ultime ricerche");
            return false;
        }
    }

    @Override
    public List<LastViewDTO> getAllViewed(String username) {
        try{
            return lastViewRepository.getLastViewsByUsername(username).stream().map(a-> modelMapper.map(a, LastViewDTO.class)).toList();

        }catch (Exception | Error e){
            System.out.println("Aidi non funziona");
            return null;
        }
    }
}
