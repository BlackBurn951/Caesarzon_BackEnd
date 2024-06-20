package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Dao.BanRepository;
import org.caesar.notificationservice.Data.Services.BanService;
import org.caesar.notificationservice.Dto.BanDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BanServiceImpl implements BanService {

    private final BanRepository banRepository;

    public List<BanDTO> getAllBans() {

    }

    @Override
    public boolean banUser(BanDTO banDTO) {

    }
}
