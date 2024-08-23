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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BanServiceImpl implements BanService {

    private final BanRepository banRepository;
    private final ModelMapper modelMapper;

    //Metodo per restituire tutti gli utenti bannati
    public List<BanDTO> getAllBans() {
        List<Ban> bans = banRepository.findAll();
        return bans.stream().map(a -> modelMapper.map(a, BanDTO.class)).toList();
    }

    @Override
    public boolean checkIfBanned(String username) {
        try{
            return banRepository.findByUserUsernameAndEndDateIsNull(username)!=null;
        }catch (Exception | Error e){
            return false;
        }
    }

    //Metodo per bannare un utente
    @Override
    public UUID validateBan(BanDTO banDTO) {
        try {
            banDTO.setConfirmed(false);
            UUID banId= banRepository.save(modelMapper.map(banDTO, Ban.class)).getId();

            return banId;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della tupla di ban");
            return null;
        }
    }

    @Override
    public boolean confirmBan(UUID banId) {
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

    //Metodo per sbannare un utente
    @Override
    public boolean sbanUser(String username, boolean confirm) {
        try {
            Ban ban;
            if(!confirm)
                ban= banRepository.findByUserUsernameAndEndDateIsNull(username);
            else
                ban= banRepository.findByUserUsernameAndConfirmedIsFalse(username);

            if(ban==null)
                return false;

            ban.setEndDate(LocalDate.now());
            ban.setConfirmed(confirm);
            banRepository.save(ban);

            return true;
        } catch (Exception | Error e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean rollback(String username) {
        try {
            Ban ban= banRepository.findByUserUsernameAndConfirmedIsFalse(username);

            if(ban==null)
                return false;

            banRepository.delete(ban);
            return true;
        } catch (Exception | Error e) {
            log.debug(e.getMessage());
            return false;
        }
    }
}
