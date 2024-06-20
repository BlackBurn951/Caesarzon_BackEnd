package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.UserNotificationRepository;
import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.caesar.notificationservice.Dto.UserNotificationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public boolean addUserNotification(UserNotificationDTO notificationDTO) {
        try{
            userNotificationRepository.save(modelMapper.map(notificationDTO, UserNotification.class));
            return true;
        }catch(Exception | Error e){
           log.debug("Errore nell'inserimento della notifica per l'utent");
           return false;
        }
    }
}
