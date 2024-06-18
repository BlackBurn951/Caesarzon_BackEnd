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
    public boolean addSupportRequest(SupportDTO supportDTO) {
        supportDTO.setSupportCode(generaCodice());

        try {
            supportRequestRepository.save(modelMapper.map(supportDTO, Support.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della richiesta");
            return false;
        }
    }

    @Override
    public boolean deleteSupportRequest(SupportDTO supportDTO) {
        try {
            supportRequestRepository.deleteBySupportCode(supportDTO.getSupportCode());

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della richiesta di supporto");
            return false;
        }
    }

    private String generaCodice() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder codice = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            codice.append(CHARACTERS.charAt(index));
        }
        return codice.toString();
    }
}
