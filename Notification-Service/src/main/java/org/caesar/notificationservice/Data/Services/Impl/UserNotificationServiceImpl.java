package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.UserNotificationRepository;
import org.caesar.notificationservice.Data.Entities.AdminNotification;
import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.caesar.notificationservice.Dto.NotificationDTO;
import org.caesar.notificationservice.Dto.UserNotificationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<NotificationDTO> getUserNotification(String username) {
        try {
            List<UserNotification> notifications= userNotificationRepository.findAllByUser(username);

            if(notifications==null || notifications.isEmpty())
                return null;

            List<NotificationDTO> notificationsDTO= notifications.stream()
                    .map(a -> modelMapper.map(a, NotificationDTO.class))
                    .toList();

            for (NotificationDTO notify: notificationsDTO) {
                notify.setId(null);
            }
            return notificationsDTO;
        } catch (Exception | Error e) {
            log.debug("Errore nella presa delle notifiche");
            return null;
        }
    }

    @Override
    public boolean addUserNotification(NotificationDTO notificationDTO, String username) {
        try{
            UserNotificationDTO userNotificationDTO= modelMapper.map(notificationDTO, UserNotificationDTO.class);
            userNotificationDTO.setUser(username);

            UserNotification userNotification= userNotificationRepository.findByDateAndSubjectAndUserAndReadAndExplanation(userNotificationDTO.getDate(),
                    userNotificationDTO.getDescription(), username, userNotificationDTO.isRead(), userNotificationDTO.getExplanation());

            if(userNotification!=null)
                userNotification.setRead(notificationDTO.isRead());



            userNotificationRepository.save(modelMapper.map(notificationDTO, UserNotification.class));
            return true;
        }catch(Exception | Error e){
           log.debug("Errore nell'inserimento della notifica per l'utent");
           return false;
        }
    }

    @Override
    public boolean deleteUserNotification(NotificationDTO notificationDTO, String username){
        try{
            return userNotificationRepository.deleteByDateAndSubjectAndUserAndReadAndExplanation(LocalDate.parse(notificationDTO.getDate()), notificationDTO.getSubject(), username, notificationDTO.isRead(), notificationDTO.getExplanation());
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

}
