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

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;

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

    @Override
    public ReportDTO getReport(UUID id) {
        return modelMapper.map(reportRepository.findById(id), ReportDTO.class);
    }

    @Override
    public List<ReportDTO> getAllReports(int num) {
        Page<Report> result = reportRepository.findAll(PageRequest.of(num, 20));
        return result.stream().map(a -> modelMapper.map(a, ReportDTO.class)).toList();
    }

    @Override
    public boolean deleteReport(UUID reviewId) {
        try {
            reportRepository.deleteByReviewId(reviewId);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return false;
        }
    }


    @Override
    public int countReportForUser(String username, UUID reviewId) {
        return reportRepository.countByUsernameUser2AndReviewId(username, reviewId);
    }

}
