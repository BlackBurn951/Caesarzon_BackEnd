package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.AdminNotificationRepository;
import org.caesar.notificationservice.Data.Entities.AdminNotification;
import org.caesar.notificationservice.Data.Services.AdminNotificationService;
import org.caesar.notificationservice.Dto.AdminNotificationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public boolean sendNotificationAllAdmin(List<AdminNotificationDTO> notification) {
        try {
            adminNotificationRepository.saveAll(notification.stream().map(a -> modelMapper.map(a, AdminNotification.class)).toList());

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel salvataggio delle notifiche per gli admin");
            return false;
        }
    }

    @Override
    public boolean deleteNotification(){}
}
