package org.caesar.notificationservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.notificationservice.Data.Dao.ReportRepository;
import org.caesar.notificationservice.Data.Entities.Report;
import org.caesar.notificationservice.Data.Services.ReportService;
import org.caesar.notificationservice.Dto.ReportDTO;
import org.caesar.notificationservice.Dto.ReviewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
            return modelMapper.map(report, ReportDTO.class);
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della segnalazione");
            return null;
        }
    }

    //Metodo per prendere una segnalazione
    @Override
    public ReportDTO getReport(UUID id) {
        return modelMapper.map(reportRepository.findById(id), ReportDTO.class);
    }

    @Override
    public boolean validateDeleteReportByUsername2(String username) {
        try {
            List<Report> reports= reportRepository.findByUsernameUser2(username);

            for(Report report: reports){
                report.setEffective(false);
            }
            reportRepository.saveAll(reports);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return false;
        }
    }

    @Override
    public List<ReportDTO> completeDeleteReportByUsername2(String username) {
        try {
            List<Report> reports= reportRepository.findByUsernameUser2(username);

            List<ReportDTO> rollbackList= reports.stream()
                    .map(a -> modelMapper.map(a, ReportDTO.class)).toList();

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

            return rollbackList;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return null;
        }
    }

    @Override
    public boolean rollbackPreCompleteByUsername2(String username) {
        try {
            List<Report> reports= reportRepository.findByUsernameUser2(username);

            for(Report report: reports){
                report.setEffective(true);
            }
            reportRepository.saveAll(reports);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return false;
        }
    }



    //Metodo per aggiungere una segnalazione tramite l'id della recensione segnalata
    @Override
    public ReportDTO getReportByReviewId(UUID id) {
        return modelMapper.map(reportRepository.findByReviewId(id), ReportDTO.class);
    }

    @Override
    public List<ReportDTO> getReportsByReviewId(UUID id) {
        try{
            List<Report> reviews= reportRepository.findByReviewId(id);

            if(reviews.isEmpty())
                return null;

            return reviews.stream()
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
            List<Report> reviews= reportRepository.findByUsernameUser2(username);

            if(reviews.isEmpty())
                return null;

            return reviews.stream()
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
        Page<Report> result = reportRepository.findAll(PageRequest.of(num, 20));
        return result.stream().map(a -> modelMapper.map(a, ReportDTO.class)).toList();
    }



    //Metodo per eliminare una segnalazione
    @Override
    public boolean validateDeleteReportByReview(UUID reviewId) {
        try {
            List<Report> reports= reportRepository.findByReviewId(reviewId);

            for(Report report: reports){
                report.setEffective(false);
            }
            reportRepository.saveAll(reports);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return false;
        }
    }

    @Override
    public List<ReportDTO> completeDeleteReportByReview(UUID reviewId) {
        try {
            List<Report> reports= reportRepository.findByReviewId(reviewId);

            List<ReportDTO> rollbackList= reports.stream()
                    .map(a -> modelMapper.map(a, ReportDTO.class)).toList();

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

            return rollbackList;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return null;
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
    public boolean rollbackPreCompleteByReview(UUID reviewId) {
        try {
            List<Report> reports= reportRepository.findByReviewId(reviewId);

            for(Report report: reports){
                report.setEffective(true);
            }
            reportRepository.saveAll(reports);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return false;
        }
    }



    @Override
    public List<ReportDTO> validateDeleteReportForUserDelete(String username, boolean rollback) {
        try {
            List<Report> reports= reportRepository.findByUsernameUser2(username);

            for(Report report: reports){
                report.setEffective(!rollback);
            }
            reportRepository.saveAll(reports);

            return reports.stream()
                    .map(a -> modelMapper.map(a, ReportDTO.class)).toList();
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return null;
        }
    }

    @Override
    public boolean completeDeleteReportForUserDelete(String username) {
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
            log.debug("Errore nella cancellazione della segnalazione");
            return false;
        }
    }



    //Metodo per contare le segnalazioni fatte ad un utente
    @Override
    public int countReportForUser(String username, UUID reviewId) {
        return reportRepository.countByUsernameUser2AndReviewId(username, reviewId);
    }

    //Metodo prendere un utente tramite username e id recensione
    @Override
    public boolean findByUsername1AndReviewId(String username, UUID reviewId) {
        return reportRepository.findByUsernameUser1AndReviewId(username, reviewId) != null;
    }

    @Override
    public boolean deleteReport(ReportDTO reportDTO) {
        try {
            reportRepository.delete(modelMapper.map(reportDTO, Report.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Sesso");
            return false;
        }
    }
}
