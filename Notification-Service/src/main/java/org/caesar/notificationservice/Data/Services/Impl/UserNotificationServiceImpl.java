package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.UserNotificationRepository;
import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.caesar.notificationservice.Dto.UserNotificationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final ModelMapper modelMapper;

    //Metodo per prendere tutte le notifiche di un utente
    @Override
    public List<UserNotificationDTO> getUserNotification(String username) {
        try {
            List<UserNotification> notifications= userNotificationRepository.findAllByUser(username);

            if(notifications==null || notifications.isEmpty())
                return null;

            return notifications.stream()
                    .map(a -> modelMapper.map(a, UserNotificationDTO.class))
                    .toList();
        } catch (Exception | Error e) {
            log.debug("Errore nella presa delle notifiche");
            return null;
        }
    }

    //Metodo per aggiungere una notifica all'utente
    @Override
    public boolean addUserNotification(UserNotificationDTO notificationDTO) {
        try{
            userNotificationRepository.save(modelMapper.map(notificationDTO, UserNotification.class));

            return true;
        }catch(Exception | Error e){
           log.debug("Errore nell'inserimento della notifica per l'utente");
           return false;
        }
    }

    //Metodo per aggiornare lo stato di lettura delle notifiche dell'utente
    @Override
    public boolean updateUserNotification(List<UserNotificationDTO> notificationDTO) {
        try{
            userNotificationRepository.saveAll(notificationDTO.stream().map(a -> modelMapper.map(a, UserNotification.class)).toList());

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'inserimento della notifica per l'utente");
            return false;
        }
    }

    //Metodo per eliminare la singola notifica dell'utente
    @Override
    public boolean deleteUserNotification(UUID id){
        try{
            userNotificationRepository.deleteById(id);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

    //Metodo per eliminare tutte le notifiche dell'utente
    @Override
    public boolean deleteAllUserNotification(String username){
        try{
            userNotificationRepository.deleteAllByUser(username);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

}
