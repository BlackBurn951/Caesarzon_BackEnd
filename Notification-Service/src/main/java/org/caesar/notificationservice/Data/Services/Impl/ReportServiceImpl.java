package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.ReportRepository;
import org.caesar.notificationservice.Data.Entities.Report;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;

    //Metodo per aggiungere una segnalazione
    @Override
    public ReportDTO addReport(ReportDTO reportDTO) {
        try {
            Report report= reportRepository.save(modelMapper.map(reportDTO, Report.class));
            report.setEffective(true);

            return modelMapper.map(report, ReportDTO.class);
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della segnalazione");
            return null;
        }
    }

    //Metodo per prendere una segnalazione
    @Override
    public ReportDTO getReport(UUID id) {
        Report report= reportRepository.findById(id).orElse(null);

        if(report==null || !report.isEffective())
            return null;

        return modelMapper.map(report, ReportDTO.class);
    }


    //Metodo per aggiungere una segnalazione tramite l'id della recensione segnalata
    @Override
    public ReportDTO getReportByReviewId(UUID id) {
        return modelMapper.map(reportRepository.findAllByReviewId(id), ReportDTO.class);
    }

    @Override
    public List<ReportDTO> getReportsByReviewId(UUID id) {
        try{
            List<Report> reports= reportRepository.findAllByReviewId(id);

            if(reports==null)
                return null;

            if(reports.isEmpty())
                return new Vector<>();

            List<Report> result= new Vector<>();
            for(Report report:reports) {
                if(!report.isEffective())
                    continue;

                result.add(report);
            }
            return result.stream()
                    .map(rev -> modelMapper.map(rev, ReportDTO.class))
                    .toList();
        }catch (Exception | Error e) {
            log.debug("Sesso3");
            return null;
        }
    }

    @Override
    public List<ReportDTO> getReportsByUsername2(String username) {
        try{
            List<Report> reports= reportRepository.findByUsernameUser2(username);

            if(reports==null)
                return null;

            if(reports.isEmpty())
                return null;

            List<Report> result= new Vector<>();
            for(Report report:reports) {
                if(!report.isEffective())
                    continue;

                result.add(report);
            }
            return result.stream()
                    .map(rev -> modelMapper.map(rev, ReportDTO.class))
                    .toList();
        }catch (Exception | Error e) {
            log.debug("Sesso3");
            return null;
        }
    }

    //Metodo per prendere tutte le segnalazioni
    @Override
    public List<ReportDTO> getAllReports(int num) {
        Page<Report> reports = reportRepository.findAll(PageRequest.of(num, 20));

        if(reports==null)
            return null;

        if(reports.get().toList().isEmpty())
            return new Vector<>();

        List<Report> result= new Vector<>();
        for(Report report:reports) {
            if(!report.isEffective())
                continue;

            result.add(report);
        }
        return result.stream().map(a -> modelMapper.map(a, ReportDTO.class)).toList();
    }



    //Metodo per eliminare una segnalazione
    @Override
    public List<ReportDTO> validateDeleteReportByReview(UUID reviewId, boolean rollback) {
        try {
            List<Report> reports= reportRepository.findAllByReviewId(reviewId);

            if(reports.isEmpty())
                return new Vector<>();

            for(Report report: reports){
                report.setEffective(rollback);
            }
            reportRepository.saveAll(reports);

            return reports.stream()
                    .map(a -> modelMapper.map(a, ReportDTO.class)).toList();
        } catch (Exception | Error e) {
            log.debug("Errore nella validazione dell'eliminazione della segnalazione per id recensione");
            return null;
        }
    }

    @Override
    public boolean completeDeleteReportByReview(UUID reviewId) {
        try {
            List<Report> reports= reportRepository.findAllByReviewId(reviewId);

            for(Report report: reports){
                report.setEffective(false);
                report.setReportDate(null);
                report.setReason(null);
                report.setDescription(null);
                report.setUsernameUser1(null);
                report.setUsernameUser2(null);
            }

            reportRepository.saveAll(reports);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel completamento dell'eliminazione della segnalazione per id recensione");
            return false;
        }
    }

    @Override
    public boolean releaseLock(List<UUID> reportsId) {
        try{
            reportRepository.deleteAllById(reportsId);

            return true;
        }catch(Exception | Error e){
            log.debug("Errore nell'eliminazione");
            return false;
        }    }



    @Override
    public List<ReportDTO> validateDeleteReportByUsername2(String username, boolean rollback) {
        try {
            List<Report> reports= reportRepository.findByUsernameUser2(username);

            if(reports.isEmpty())
                return new Vector<>();

            List<Report> result= new Vector<>();
            for(Report report: reports){
                if(!report.isEffective() && !rollback)
                    continue;
                report.setEffective(!rollback);

                result.add(report);
            }
            reportRepository.saveAll(result);

            return result.stream()
                    .map(a -> modelMapper.map(a, ReportDTO.class)).toList();
        } catch (Exception | Error e) {
            log.debug("Errore nella validazione della cancellazione delle segnalazioni per username utente segnalato");
            return null;
        }
    }

    @Override
    public boolean completeDeleteReportByUsername2(String username) {
        try {
            List<Report> reports= reportRepository.findByUsernameUser2(username);

            for(Report report: reports){
                report.setEffective(false);
                report.setReportDate(null);
                report.setReason(null);
                report.setDescription(null);
                report.setReviewId(null);
                report.setUsernameUser1(null);
                report.setUsernameUser2(null);
            }

            reportRepository.saveAll(reports);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel completamento dell'eliminazione delle segnalazioni per username utente segnalato");
            return false;
        }
    }



    //Metodo per contare le segnalazioni fatte ad un utente
    @Override
    public int countReportForUser(String username) {
        return reportRepository.countByUsernameUser2(username);
    }

    //Metodo prendere un utente tramite username e id recensione
    @Override
    public boolean findByUsername1AndReviewId(String username, UUID reviewId) {
        return reportRepository.findByUsernameUser1AndReviewIdAndEffectiveIsTrue(username, reviewId) != null;
    }

    @Override
    public boolean deleteReport(ReportDTO reportDTO) {
        try {
            Report report= reportRepository.findById(reportDTO.getId()).orElse(null);

            if(report==null || !report.isEffective())
                return false;

            reportRepository.delete(modelMapper.map(reportDTO, Report.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Sesso");
            return false;
        }
    }
}
