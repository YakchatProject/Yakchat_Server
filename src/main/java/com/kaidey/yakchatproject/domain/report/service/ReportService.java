package com.kaidey.yakchatproject.domain.report.service;

import com.kaidey.yakchatproject.domain.report.dto.ReportDto;
import com.kaidey.yakchatproject.domain.report.entity.Report;
import com.kaidey.yakchatproject.domain.user.entity.User;
import com.kaidey.yakchatproject.domain.report.entity.ReportStatus;
import com.kaidey.yakchatproject.global.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.domain.report.repository.ReportRepository;
import com.kaidey.yakchatproject.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReportDto createReport(ReportDto reportDto) {
        User reporter = userRepository.findById(reportDto.getReporterId())
                .orElseThrow(() -> new EntityNotFoundException("Reporter not found"));
        User reportedUser = userRepository.findById(reportDto.getReportedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Reported user not found"));

        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setReason(reportDto.getReason());
        report.setEvidence(reportDto.getEvidence());

        Report savedReport = reportRepository.save(report);
        return convertToDto(savedReport);
    }

    @Transactional
    public List<ReportDto> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDto updateReportStatus(Long id, ReportStatus status, String handler, String result) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));
        report.setStatus(status);
        report.setHandler(handler);
        report.setResult(result);
        Report updatedReport = reportRepository.save(report);
        return convertToDto(updatedReport);
    }

    private ReportDto convertToDto(Report report) {
        ReportDto reportDto = new ReportDto();
        reportDto.setId(report.getId());
        reportDto.setReporterId(report.getReporter().getId());
        reportDto.setReportedUserId(report.getReportedUser().getId());
        reportDto.setReason(report.getReason());
        reportDto.setReportedAt(report.getReportedAt());
        reportDto.setStatus(report.getStatus().name());
        reportDto.setEvidence(report.getEvidence());
        reportDto.setHandler(report.getHandler());
        reportDto.setResult(report.getResult());
        return reportDto;
    }
}