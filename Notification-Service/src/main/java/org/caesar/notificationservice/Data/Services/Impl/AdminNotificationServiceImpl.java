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
                if(!notify.isConfirmed())
                    continue;

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
            adminNotificationRepository.saveAll(notification.stream()
                                                            .map(a -> {
                                                                AdminNotification not= modelMapper.map(a, AdminNotification.class);
                                                                not.setConfirmed(true);

                                                                return not;
                                                            }).toList());

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
            AdminNotification notification= adminNotificationRepository.findById(id).orElse(null);

            if(notification==null || !notification.isConfirmed())
                return false;

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
            List<AdminNotification> notification= adminNotificationRepository.findAllBySupport(modelMapper.map(supportDTO, Support.class));

            if(notification==null)
                return false;

            if(notification.isEmpty())
                return true;

            for(AdminNotification notify: notification) {
                if (!notify.isConfirmed())
                    continue;
                adminNotificationRepository.deleteById(notify.getId());
            }

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione");
            return false;
        }
    }



    @Override
    public List<SaveAdminNotificationDTO> validateDeleteByReport(ReportDTO reportDTO, boolean rollback) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            List<AdminNotification> result= new Vector<>();
            for(AdminNotification notify: notifications) {
                if(!notify.isConfirmed() && !rollback)
                    continue;

                notify.setConfirmed(rollback);
                result.add(notify);
            }

            if (result.isEmpty())
                return new Vector<>();

            adminNotificationRepository.saveAll(result);
            return result.stream()
                    .map(notify -> {
                        SaveAdminNotificationDTO not= new SaveAdminNotificationDTO();
                        not.setId(notify.getId());
                        not.setAdmin(notify.getAdmin());
                        not.setRead(notify.isRead());
                        not.setSubject(notify.getSubject());
                        not.setDate(notify.getDate());
                        not.setSupport(null);
                        not.setReport(modelMapper.map(notify.getReport(), ReportDTO.class));
                        not.setConfirmed(!rollback);

                        return not;
                    }).toList();
        }catch(Exception | Error e){
            log.debug("Errore nella validazione dell'eliminazione per segnalazione");
            return null;
        }

    }

    @Override
    public boolean completeDeleteByReport(ReportDTO reportDTO) {
        try{
            System.out.println("Segnalazione arrivata per eliminare: "+reportDTO.getReason());
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            System.out.println("Post presa lista notifiche");
            for(AdminNotification notify: notifications) {
                System.out.println("Admin a cui è indirizzata la notifica: "+notify.getAdmin());
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
            System.out.println(e);
            log.debug("Errore nel completamento dell'eliminazione per segnalazione");
            return false;
        }
    }

    @Override
    public boolean releaseLock(List<UUID> notificationIds) {
        try{
            adminNotificationRepository.deleteAllById(notificationIds);

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nel rilascio dei lock");
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
            log.debug("Errore nel rollback pre completamento per segnalazione");
            return false;
        }
    }



    @Override
    public boolean validateOrRollbackDeleteBySupports(SupportDTO support, boolean rollback) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllBySupport(modelMapper.map(support, Support.class));

            List<AdminNotification> result= new Vector<>();
            for(AdminNotification notify: notifications) {
                if(!notify.isConfirmed() && !rollback)
                    continue;

                notify.setConfirmed(rollback);
                result.add(notify);
            }

            if (result.isEmpty())
                return true;

            adminNotificationRepository.saveAll(result);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nela validazione dell'eliminazione per richiesta di supporto");
            return false;
        }
    }

    @Override
    public boolean validateOrRollbackDeleteByReportsOnUserDelete(ReportDTO report, boolean rollback) {
        try{
            List<AdminNotification> notifications= adminNotificationRepository.findAllByReport(modelMapper.map(report, Report.class));

            List<AdminNotification> result= new Vector<>();
            for(AdminNotification notify: notifications) {
                if(!notify.isConfirmed() && !rollback)
                    continue;

                notify.setConfirmed(rollback);
                result.add(notify);
            }

            if (result.isEmpty())
                return true;

            adminNotificationRepository.saveAll(result);
            return true;
        }catch(Exception | Error e){
            log.debug("Errore nella validazione dell'eliminazione per segnalazione");
            return false;
        }
    }

    //Metodo per aggiornare lo stato di lettura delle notifiche dell'admin
    @Override
    public boolean updateAdminNotification(List<SaveAdminNotificationDTO> notificationDTO) {
        try{
            adminNotificationRepository.saveAll(notificationDTO.stream()
                                                                .map(a -> {
                                                                    AdminNotification not= modelMapper.map(a, AdminNotification.class);
                                                                    not.setConfirmed(true);

                                                                    return not;
                                                                }).toList());

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'inserimento della notifica per l'admin");
            return false;
        }
    }

    @Override
    public boolean deleteByReport(ReportDTO reportDTO) {
        try{
            List<AdminNotification> notification= adminNotificationRepository.findAllByReport(modelMapper.map(reportDTO, Report.class));

            if(notification==null)
                return false;

            if(notification.isEmpty())
                return true;

            for(AdminNotification notify: notification) {
                if (!notify.isConfirmed())
                    continue;
                adminNotificationRepository.deleteById(notify.getId());
            }

            return true;
        }catch (Exception | Error e){
            log.debug("sesso2");
            return false;
        }
    }
}
