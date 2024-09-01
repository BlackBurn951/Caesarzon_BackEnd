package org.caesar.notificationservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.caesar.notificationservice.Data.Dao.UserNotificationRepository;
import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.caesar.notificationservice.Dto.UserNotificationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

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

            if(notifications==null || notifications.isEmpty()) {
                return null;
            }

            List<UserNotification> result= new Vector<>();
            for(UserNotification notification: notifications) {
                if(!notification.isConfirmed())
                    continue;
                result.add(notification);
            }

            List<UserNotificationDTO> notificationDTO= result.stream()
                    .map(a -> modelMapper.map(a, UserNotificationDTO.class))
                    .toList();


            for(UserNotificationDTO notify: notificationDTO) {
                System.out.println(notify.getDate());
                String date= String.valueOf(notify.getDate());
                notify.setDate(date);
            }

            return notificationDTO;


        } catch (Exception | Error e) {
            log.debug("Errore nella presa delle notifiche");
            return null;
        }
    }

    //Metodo per aggiungere una notifica all'utente
    @Override
    public boolean addUserNotification(UserNotificationDTO notificationDTO) {
        try{
            UserNotification notification= new UserNotification();

            notification.setDate(LocalDate.now());
            notification.setId(notificationDTO.getId());
            notification.setUser(notificationDTO.getUser());
            notification.setSubject(notificationDTO.getSubject());
            notification.setExplanation(notificationDTO.getExplanation());
            notification.setRead(notificationDTO.isRead());
            notification.setConfirmed(true);
            userNotificationRepository.save(notification);

            return true;
        }catch(Exception | Error e){
           log.debug("Errore nell'inserimento della notifica per l'utente");
           return false;
        }
    }

    @Override
    public UUID validateNotification() {
        try{
            UserNotification notification= new UserNotification();

            notification.setDate(null);
            notification.setUser(null);
            notification.setSubject(null);
            notification.setExplanation(null);
            notification.setRead(false);
            notification.setConfirmed(false);

            UUID notifyId= userNotificationRepository.save(notification).getId();

            return notifyId;
        }catch(Exception | Error e){
            System.out.println(e);
            log.debug("Errore nell'inserimento della notifica per l'utente");
            return null;
        }
    }

    @Override
    public boolean completeNotification(UserNotificationDTO userNotificationDTO) {
        try{
            UserNotification notification= new UserNotification();

            notification.setDate(LocalDate.now());
            notification.setId(userNotificationDTO.getId());
            notification.setUser(userNotificationDTO.getUser());
            notification.setSubject(userNotificationDTO.getSubject());
            notification.setExplanation(userNotificationDTO.getExplanation());
            notification.setRead(userNotificationDTO.isRead());
            notification.setConfirmed(false);

            userNotificationRepository.save(notification);

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'inserimento della notifica per l'utente");
            return false;
        }
    }

    @Override
    public boolean releaseNotification(UUID notificationId) {
        try{
            UserNotification notification= userNotificationRepository.findById(notificationId).orElse(null);

            if(notification==null)
                return false;

            notification.setConfirmed(true);

            userNotificationRepository.save(notification);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'inserimento della notifica per l'utente");
            return false;
        }
    }



    @Override
    public boolean validateOrRollbackDeleteUserNotifications(String username, boolean rollback) {
        try{
            List<UserNotification> notifications= userNotificationRepository.findAllByUser(username);

            if(notifications.isEmpty())
                return true;

            List<UserNotification> result= new Vector<>();
            for(UserNotification notify: notifications) {
                if(!notify.isConfirmed() && !rollback)
                    continue;

                notify.setConfirmed(rollback);
                result.add(notify);
            }

            if(result.isEmpty())
                return true;

            userNotificationRepository.saveAll(result);

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
            List<UserNotification> notification= new Vector<>();

            for(UserNotificationDTO user: notificationDTO){
                UserNotification userNot = userNotificationRepository.findById(user.getId()).orElse(null);
                if(userNot == null && !userNot.isConfirmed())
                    continue;
                userNot.setRead(true);


                notification.add(userNot);
            }

            if(notification.isEmpty())
                return false;

            userNotificationRepository.saveAll(notification);

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
            UserNotification notification= userNotificationRepository.findById(id).orElse(null);

            if(notification==null || !notification.isConfirmed())
                return false;

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
            List<UserNotification> notifications= userNotificationRepository.findAllByUser(username);

            List<UserNotification> result= new Vector<>();
            for(UserNotification notification: notifications){
                if(!notification.isConfirmed())
                    continue;
                result.add(notification);
            }

            if(result.isEmpty())
                return false;
            userNotificationRepository.deleteAll(result);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }
}
