package org.caesar.notificationservice.Data.Services.Impl;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class BanServiceImpl implements BanService {

    private final BanRepository banRepository;
    private final ModelMapper modelMapper;

    public List<BanDTO> getAllBans() {
        List<Ban> bans = banRepository.findAll();
        return bans.stream().map(a -> modelMapper.map(a, BanDTO.class)).toList();
    }

    @Override
    public boolean banUser(BanDTO banDTO) {
        try {
            banRepository.save(modelMapper.map(banDTO, Ban.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della tupla di ban");
            return false;
        }
    }

    @Override
    public boolean sbanUser(String username) {
        try {
            Ban ban= banRepository.findByUserUsername(username);

            if(ban==null)
                return false;

            ban.setEndDate(LocalDate.now());
            banRepository.save(ban);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della tupla di ban");
            return false;
        }
    }
}
