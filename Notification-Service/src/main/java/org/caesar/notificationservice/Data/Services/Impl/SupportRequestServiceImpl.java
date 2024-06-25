package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.SupportRequestRepository;
import org.caesar.notificationservice.Data.Entities.Support;
import org.caesar.notificationservice.Data.Services.SupportRequestService;
import org.caesar.notificationservice.Dto.SupportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<SupportDTO> getAllSupportRequest(int num) {
        Page<Support> result = supportRequestRepository.findAll(PageRequest.of(num, 20));
        return result.stream().map(a -> modelMapper.map(a, SupportDTO.class)).toList();
    }

    @Override
    public SupportDTO getSupport(UUID id) {
        Support support = supportRequestRepository.findById(id).orElse(null);
        assert support != null;
        System.out.println("sono nella get del support: " + support.getId());
        return modelMapper.map(supportRequestRepository.findById(id), SupportDTO.class);
    }

    @Override
    public SupportDTO addSupportRequest(SupportDTO supportDTO) {
        try {
            Support support = supportRequestRepository.save(modelMapper.map(supportDTO, Support.class));
            return modelMapper.map(support, SupportDTO.class);
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della richiesta");
            return null;
        }
    }

    @Override
    public boolean deleteSupportRequest(SupportDTO supportDTO) {
        try {
            supportRequestRepository.deleteById(supportDTO.getId());
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della richiesta di supporto");
            return false;
        }
    }


}
