package org.caesar.notificationservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.UserNotificationRepository;
import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.caesar.notificationservice.Dto.UserNotificationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final ModelMapper modelMapper;

    private final static String USER_NOTIFICATION= "userNotificationService";


    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su address service da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }

    //Metodo per prendere tutte le notifiche di un utente
    @Override
    @Retry(name=USER_NOTIFICATION)
    public List<UserNotificationDTO> getUserNotification(String username) {
        try {
            List<UserNotification> notifications= userNotificationRepository.findAllByUser(username);  //CANCELLARE DOPO LA VENTASIMA TUPLA

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
//    @CircuitBreaker(name=USER_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=USER_NOTIFICATION)
    public boolean addUserNotification(UserNotificationDTO notificationDTO) {
        try{
            System.out.println("user: " + notificationDTO.getUser());
            System.out.println("data: " + notificationDTO.getDate());
            System.out.println("read: " + notificationDTO.isRead());
            System.out.println("explanation: " + notificationDTO.getExplanation());
            System.out.println("subj: " + notificationDTO.getSubject());
            notificationDTO.setDate(LocalDate.now());
            userNotificationRepository.save(modelMapper.map(notificationDTO, UserNotification.class));

            return true;
        }catch(Exception | Error e){
           log.debug("Errore nell'inserimento della notifica per l'utente");
           return false;
        }
    }

    //Metodo per aggiornare lo stato di lettura delle notifiche dell'utente
    @Override
    @CircuitBreaker(name=USER_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=USER_NOTIFICATION)
    public boolean updateUserNotification(List<UserNotificationDTO> notificationDTO) {
        try{
            for(UserNotificationDTO user: notificationDTO){
                UserNotification userNot = userNotificationRepository.findById(user.getId()).orElse(null);
                assert userNot != null;
                user.setDate(userNot.getDate());
                user.setRead(true);
            }
            userNotificationRepository.saveAll(notificationDTO.stream().map(a -> modelMapper.map(a, UserNotification.class)).toList());

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'inserimento della notifica per l'utente");
            return false;
        }
    }

    //Metodo per eliminare la singola notifica dell'utente
    @Override
    @CircuitBreaker(name=USER_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=USER_NOTIFICATION)
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
    @CircuitBreaker(name=USER_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=USER_NOTIFICATION)
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
