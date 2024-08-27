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
    public List<SaveAdminNotificationDTO> validateDeleteByReport(ReportDTO reportDTO) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            for(AdminNotification notify: notifications) {
                notify.setConfirmed(false);
            }

            adminNotificationRepository.saveAll(notifications);
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
    public boolean completeDeleteByReport(ReportDTO reportDTO) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            for(AdminNotification notify: notifications) {
                notify.setDate(null);
                notify.setAdmin(null);
                notify.setRead(false);
                notify.setConfirmed(false);
                notify.setSubject(null);
                notify.setSupport(null);
            }

            adminNotificationRepository.saveAll(notifications);

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }

    @Override
    public boolean releaseLock(List<UUID> notificationIds) {
        try{
            adminNotificationRepository.deleteAllById(notificationIds);

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
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



    @Override
    public List<SaveAdminNotificationDTO> validateOrRollbackDeleteBySupports(SupportDTO support, boolean rollback) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllBySupport(modelMapper.map(support, Support.class));

            for(AdminNotification notify: notifications) {
                notify.setConfirmed(!rollback);
            }

            adminNotificationRepository.saveAll(notifications);
            return notifications.stream()
                    .map(notify -> {
                        SaveAdminNotificationDTO not= new SaveAdminNotificationDTO();
                        not.setId(notify.getId());
                        not.setAdmin(notify.getAdmin());
                        not.setRead(notify.isRead());
                        not.setSubject(notify.getSubject());
                        not.setDate(notify.getDate());
                        not.setSupport(modelMapper.map(notify.getSupport(), SupportDTO.class));
                        not.setReport(null);
                        not.setConfirmed(true);

                        return not;
                    }).toList();
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return null;
        }
    }

    @Override
    public boolean completeDeleteBySupports(SupportDTO support) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllBySupport(modelMapper.map(support, Support.class));

            for(AdminNotification notify: notifications) {
                notify.setDate(null);
                notify.setAdmin(null);
                notify.setRead(false);
                notify.setConfirmed(false);
                notify.setSubject(null);
                notify.setReport(null);
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

    @Override
    public boolean deleteByReport(ReportDTO reportDTO) {
        try{
            adminNotificationRepository.deleteByReport(modelMapper.map(reportDTO, Report.class));

            return true;
        }catch (Exception | Error e){
            log.debug("sesso2");
            return false;
        }
    }
}
