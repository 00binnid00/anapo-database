package com.example.anapo.admin.application.controller;

import com.example.anapo.admin.application.service.ReportActionService;
import com.example.anapo.user.application.report.dto.ReportResponseDto;
import com.example.anapo.user.domain.report.repository.ReportRepository;
import com.example.anapo.admin.application.dto.ReportProcessDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportRepository reportRepository;
    private final ReportActionService actionService;

    @GetMapping
    public List<ReportResponseDto> findAll() {
        return reportRepository.findAll().stream()
                .map(ReportResponseDto::from)
                .toList();
    }

    @PatchMapping("/{reportId}")
    public ResponseEntity<?> process(
            @PathVariable Long reportId,
            @RequestBody ReportProcessDto dto
    ) {
        return ResponseEntity.ok(
                actionService.process(reportId, dto)
        );
    }
}
