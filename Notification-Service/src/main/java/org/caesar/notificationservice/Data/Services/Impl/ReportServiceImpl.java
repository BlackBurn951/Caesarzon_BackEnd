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

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;

    @Override
    public boolean addReport(ReportDTO reportDTO) {
        reportDTO.setReportCode(generaCodice());
        try {
            reportRepository.save(modelMapper.map(reportDTO, Report.class));

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della segnalazione");
            return false;
        }
    }

    @Override
    public ReportDTO getReport(String reportCode) {
        return modelMapper.map(reportRepository.findByReportCode(reportCode), ReportDTO.class);
    }

    @Override
    public List<ReportDTO> getAllReports(int num) {
        Page<Report> result = reportRepository.findAll(PageRequest.of(num, 20));
        return result.stream().map(a -> modelMapper.map(a, ReportDTO.class)).toList();
    }

    @Override
    public boolean deleteReport(ReportDTO reportDTO) {
        try {
            reportRepository.deleteByReportCode(reportDTO.getReportCode());
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della segnalazione");
            return false;
        }
    }


    //Metodi di servizio
    private String generaCodice() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder codice = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            codice.append(CHARACTERS.charAt(index));
        }
        return codice.toString();
    }
}
