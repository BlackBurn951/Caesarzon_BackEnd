package org.caesar.notificationservice.Data.Services.Impl;

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

    //Metodo per prendere le notifiche dell'admin
    @Override
    public List<AdminNotificationDTO> getAdminNotification(String username) {
        try {

            List<AdminNotification> notifications= adminNotificationRepository.findAllByAdmin(username);

            if(notifications==null || notifications.isEmpty())
                return null;


            List<AdminNotificationDTO> result= new Vector<>();
            AdminNotificationDTO notificationDTO;

            for(AdminNotification notify: notifications) {
                notificationDTO= new AdminNotificationDTO();

                notificationDTO.setId(notify.getId());
                notificationDTO.setAdmin(notify.getAdmin());
                notificationDTO.setRead(notify.isRead());
                notificationDTO.setSubject(notify.getSubject());
                notificationDTO.setDate(notify.getDate().toString());

                if(notify.getSupport()==null)
                    notificationDTO.setReportId(notify.getReport().getId());
                else
                    notificationDTO.setSupportId(notify.getSupport().getId());

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
    public boolean deleteBySupport(SupportDTO supportDTO) {
        try{
            adminNotificationRepository.deleteBySupport(modelMapper.map(supportDTO, Support.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

    @Override
    public boolean validateDeleteByReport(ReportDTO reportDTO) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            for(AdminNotification notify: notifications) {
                notify.setConfirmed(false);
            }

            adminNotificationRepository.saveAll(notifications);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }

    }

    @Override
    public List<SaveAdminNotificationDTO> completeDeleteByReport(ReportDTO reportDTO) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            adminNotificationRepository.deleteAll(notifications);

            return notifications.stream()
                    .map(notify -> {
                        SaveAdminNotificationDTO not= new SaveAdminNotificationDTO();
                        not.setId(notify.getId());
                        not.setAdmin(notify.getAdmin());
                        not.setRead(notify.isRead());
                        not.setSubject(notify.getSubject());
                        not.setDate(notify.getDate());
                        not.setSupport(null);
                        not.setReport(modelMapper.map(notify.getReport(), ReportDTO.class));
                        not.setConfirmed(true);

                        return not;
                    }).toList();
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return null;
        }
    }

    @Override
    public boolean rollbackPreComplete(ReportDTO reportDTO) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            for(AdminNotification notify: notifications) {
                notify.setConfirmed(true);
            }

            adminNotificationRepository.saveAll(notifications);

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

    //Metodo per aggiornare lo stato di lettura delle notifiche dell'admin
    @Override
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
