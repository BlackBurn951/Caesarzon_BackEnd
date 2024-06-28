package org.caesar.notificationservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.AdminNotificationRepository;
import org.caesar.notificationservice.Data.Entities.AdminNotification;
import org.caesar.notificationservice.Data.Entities.Report;
import org.caesar.notificationservice.Data.Entities.Support;
import org.caesar.notificationservice.Data.Services.AdminNotificationService;
import org.caesar.notificationservice.Dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;
    private final ModelMapper modelMapper;

    private final static String ADMIN_NOTIFICATION= "adminNotificationService";


    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su address service da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }


    //Metodo per prendere le notifiche dell'admin
    @Override
    @Retry(name=ADMIN_NOTIFICATION)
    public List<AdminNotificationDTO> getAdminNotification(String username) {
        try {
            List<AdminNotification> notifications= adminNotificationRepository.findAllByAdmin(username);

            if(notifications==null || notifications.isEmpty())
                return null;

            List<SaveAdminNotificationDTO> firstDTO= notifications.stream().map(a -> modelMapper.map(a, SaveAdminNotificationDTO.class)).toList();

            List<AdminNotificationDTO> result= new Vector<>();
            AdminNotificationDTO notificationDTO;
            for(SaveAdminNotificationDTO notify: firstDTO) {
                notificationDTO= new AdminNotificationDTO();

                notificationDTO.setId(notify.getId());
                notificationDTO.setAdmin(notify.getAdmin());
                notificationDTO.setRead(notify.isRead());
                notificationDTO.setSubject(notify.getSubject());
                notificationDTO.setDate(notify.getDate());

                if(notify.getSupport()==null)
                    notificationDTO.setReportId(notify.getReport().getId());
                else
                    notificationDTO.setSupportId(notify.getReport().getId());

                result.add(notificationDTO);
            }
            return result;
        } catch (Exception | Error e) {
            log.debug("Errore nella presa delle notifiche");
            return null;
        }
    }

    //Metodo per inviare le notifiche all'admin
    @Override
    @CircuitBreaker(name=ADMIN_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADMIN_NOTIFICATION)
    public boolean sendNotificationAllAdmin(List<SaveAdminNotificationDTO> notification) {
        try {
            adminNotificationRepository.saveAll(notification.stream().map(a -> modelMapper.map(a, AdminNotification.class)).toList());

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel salvataggio delle notifiche per gli admin");
            return false;
        }
    }

    //Metodo per eliminare le notifiche dell'admin
    @Override
    @CircuitBreaker(name=ADMIN_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADMIN_NOTIFICATION)
    public boolean deleteAdminNotification(UUID id){
        try{
            adminNotificationRepository.deleteById(id);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

    //Metodo per eliminare le notifiche dell'admin tramite richiesta di supporto
    @Override
    @CircuitBreaker(name=ADMIN_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADMIN_NOTIFICATION)
    public boolean deleteBySupport(SupportDTO supportDTO) {
        try{
            adminNotificationRepository.deleteBySupport(modelMapper.map(supportDTO, Support.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

    //Metodo per eliminare le notifiche dell'admin tramite segnalazione
    @Override
    @CircuitBreaker(name=ADMIN_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADMIN_NOTIFICATION)
    public boolean deleteByReport(ReportDTO reportDTO) {
        try{
            adminNotificationRepository.deleteByReport(modelMapper.map(reportDTO, Report.class));
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }

    }

    //Metodo per aggiornare lo stato di lettura delle notifiche dell'admin
    @Override
    @CircuitBreaker(name=ADMIN_NOTIFICATION, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADMIN_NOTIFICATION)
    public boolean updateAdminNotification(List<SaveAdminNotificationDTO> notificationDTO) {
        try{
            adminNotificationRepository.saveAll(notificationDTO.stream().map(a -> modelMapper.map(a, AdminNotification.class)).toList());

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'inserimento della notifica per l'admin");
            return false;
        }
    }
}
