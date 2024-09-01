package org.caesar.notificationservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.BanRepository;
import org.caesar.notificationservice.Data.Entities.Ban;
import org.caesar.notificationservice.Data.Services.BanService;
import org.caesar.notificationservice.Dto.BanDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class BanServiceImpl implements BanService {

    private final BanRepository banRepository;
    private final ModelMapper modelMapper;

    //Metodo per restituire tutti gli utenti bannati
    public List<BanDTO> getAllBans(@RequestParam("num") int num) {
        Page<Ban> bans = banRepository.findAllByEndDateIsNull(PageRequest.of(num, 20));

        List<BanDTO> result= new Vector<>();
        for(Ban ban : bans) {
            if(!ban.isConfirmed())
                continue;
            result.add(modelMapper.map(ban, BanDTO.class));
        }

        return result;
    }


    @Override
    public boolean checkIfBanned(String username) {
        try{
            Ban result= banRepository.findByUserUsernameAndEndDateIsNull(username);

            return result!=null && result.isConfirmed();
        }catch (Exception | Error e){
            return false;
        }
    }


    //Metodi per bannare un utente
    @Override
    public UUID validateBan() {
        try {
            Ban ban= new Ban();
            UUID banId= banRepository.save(ban).getId();

            return banId;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della tupla di ban");
            return null;
        }
    }

    @Override
    public boolean confirmBan(BanDTO banDTO) {
        try {
            Ban ban= banRepository.findById(banDTO.getId()).orElse(null);

            if(ban==null)
                return false;

            banRepository.save(modelMapper.map(banDTO, Ban.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della tupla di ban");
            return false;
        }
    }



    //Metodi per concludere un operazione di ban/sban
    @Override
    public boolean releaseLock(UUID banId) {
        try {
            Ban ban= banRepository.findById(banId).orElse(null);

            if(ban==null)
                return false;

            ban.setConfirmed(true);
            banRepository.save(ban);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della tupla di ban");
            return false;
        }
    }

    @Override
    public boolean rollback(UUID banId) {
        try {
            if(banId==null)
                return false;

            banRepository.deleteById(banId);
            return true;
        } catch (Exception | Error e) {
            log.debug(e.getMessage());
            return false;
        }
    }



    //Metodi per lo sban di un utente
    @Override
    public UUID validateSbanUser(String username) {
        try {
            Ban ban= banRepository.findByUserUsernameAndEndDateIsNull(username);

            if(ban==null)
                return null;

            ban.setConfirmed(false);
            banRepository.save(ban);

            return ban.getId();
        } catch (Exception | Error e) {
            log.debug(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean completeSbanUser(String username) {
        try {
            Ban ban= banRepository.findByUserUsernameAndEndDateIsNull(username);

            if(ban==null)
                return false;

            ban.setEndDate(LocalDate.now());
            banRepository.save(ban);

            return true;
        } catch (Exception | Error e) {
            log.debug(e.getMessage());
            return false;
        }
    }
}
