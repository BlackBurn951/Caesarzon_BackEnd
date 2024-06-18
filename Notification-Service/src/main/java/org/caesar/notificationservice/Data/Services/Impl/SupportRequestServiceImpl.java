package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.SupportRequestRepository;
import org.caesar.notificationservice.Data.Entities.SupportRequest;
import org.caesar.notificationservice.Data.Services.SupportRequestService;
import org.caesar.notificationservice.Dto.SupportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<SupportDTO> getAllSupportRequest(int num) {
        Page<SupportRequest> result = supportRequestRepository.findAll(PageRequest.of(num, 20));
        return result.stream().map(a -> modelMapper.map(a, SupportDTO.class)).toList();
    }

    @Override
    public boolean addSupportRequest(SupportDTO supportDTO) {
        try {
            supportRequestRepository.save(modelMapper.map(supportDTO, SupportRequest.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della richiesta");
            return false;
        }
    }

    @Override
    public boolean deleteSupportRequest(SupportDTO supportDTO) {
        try {
            supportRequestRepository.deleteByDateRequestAndTypeAndTextAndSubjectAndUsername(supportDTO.getDateRequest(),
                    supportDTO.getType(), supportDTO.getText(),
                    supportDTO.getSubject(), supportDTO.getUsername());

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della richiesta di supporto");
            return false;
        }
    }
}
