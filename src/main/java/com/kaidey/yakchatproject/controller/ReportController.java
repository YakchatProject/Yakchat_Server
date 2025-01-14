package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.ReportDto;
import com.kaidey.yakchatproject.entity.enums.ReportStatus;
import com.kaidey.yakchatproject.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportDto> createReport(@RequestBody ReportDto reportDto) {
        ReportDto newReport = reportService.createReport(reportDto);
        return ResponseEntity.ok(newReport);
    }

    @GetMapping
    public ResponseEntity<List<ReportDto>> getAllReports() {
        List<ReportDto> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReportDto> updateReportStatus(
            @PathVariable Long id,
            @RequestParam ReportStatus status,
            @RequestParam String handler,
            @RequestParam String result) {
        ReportDto updatedReport = reportService.updateReportStatus(id, status, handler, result);
        return ResponseEntity.ok(updatedReport);
    }
}