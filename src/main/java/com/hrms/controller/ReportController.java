package com.hrms.controller;

import com.hrms.dto.*;
import com.hrms.service.ReportExportService;
import com.hrms.service.ReportService;
import com.hrms.util.CustomApiResponse;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportExportService reportExportService;

    @GetMapping("/attendance")
    public ResponseEntity<CustomApiResponse<List<AttendanceReportRowDto>>> getAttendanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) {
        List<AttendanceReportRowDto> data = reportService.getAttendanceReport(startDate, endDate, departmentId);
        return ResponseEntity.ok(CustomApiResponse.<List<AttendanceReportRowDto>>builder().data(data).build());
    }

    @GetMapping("/leave")
    public ResponseEntity<CustomApiResponse<List<LeaveReportRowDto>>> getLeaveReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) {
        List<LeaveReportRowDto> data = reportService.getLeaveReport(startDate, endDate, departmentId);
        return ResponseEntity.ok(CustomApiResponse.<List<LeaveReportRowDto>>builder().data(data).build());
    }

    @GetMapping("/overtime")
    public ResponseEntity<CustomApiResponse<List<OvertimeReportRowDto>>> getOvertimeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) {
        List<OvertimeReportRowDto> data = reportService.getOvertimeReport(startDate, endDate, departmentId);
        return ResponseEntity.ok(CustomApiResponse.<List<OvertimeReportRowDto>>builder().data(data).build());
    }

    @GetMapping("/claim")
    public ResponseEntity<CustomApiResponse<List<ClaimReportRowDto>>> getClaimReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) {
        List<ClaimReportRowDto> data = reportService.getClaimReport(startDate, endDate, departmentId);
        return ResponseEntity.ok(CustomApiResponse.<List<ClaimReportRowDto>>builder().data(data).build());
    }

    @GetMapping("/employee-summary")
    public ResponseEntity<CustomApiResponse<List<EmployeeSummaryRowDto>>> getEmployeeSummaryReport(
            @RequestParam(required = false) Integer departmentId) {
        List<EmployeeSummaryRowDto> data = reportService.getEmployeeSummaryReport(departmentId);
        return ResponseEntity.ok(CustomApiResponse.<List<EmployeeSummaryRowDto>>builder().data(data).build());
    }

    // --- Export endpoints ---

    @GetMapping(value = "/attendance/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportAttendanceExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException {
        byte[] data = reportExportService.exportAttendanceToExcel(startDate, endDate, departmentId);
        return buildFileResponse(data, "attendance-report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping(value = "/attendance/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportAttendancePdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException, DocumentException {
        byte[] data = reportExportService.exportAttendanceToPdf(startDate, endDate, departmentId);
        return buildFileResponse(data, "attendance-report.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    @GetMapping(value = "/leave/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportLeaveExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException {
        byte[] data = reportExportService.exportLeaveToExcel(startDate, endDate, departmentId);
        return buildFileResponse(data, "leave-report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping(value = "/leave/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportLeavePdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException, DocumentException {
        byte[] data = reportExportService.exportLeaveToPdf(startDate, endDate, departmentId);
        return buildFileResponse(data, "leave-report.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    @GetMapping(value = "/overtime/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportOvertimeExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException {
        byte[] data = reportExportService.exportOvertimeToExcel(startDate, endDate, departmentId);
        return buildFileResponse(data, "overtime-report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping(value = "/overtime/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportOvertimePdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException, DocumentException {
        byte[] data = reportExportService.exportOvertimeToPdf(startDate, endDate, departmentId);
        return buildFileResponse(data, "overtime-report.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    @GetMapping(value = "/claim/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportClaimExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException {
        byte[] data = reportExportService.exportClaimToExcel(startDate, endDate, departmentId);
        return buildFileResponse(data, "claim-report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping(value = "/claim/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportClaimPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer departmentId) throws IOException, DocumentException {
        byte[] data = reportExportService.exportClaimToPdf(startDate, endDate, departmentId);
        return buildFileResponse(data, "claim-report.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    @GetMapping(value = "/employee-summary/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportEmployeeSummaryExcel(
            @RequestParam(required = false) Integer departmentId) throws IOException {
        byte[] data = reportExportService.exportEmployeeSummaryToExcel(departmentId);
        return buildFileResponse(data, "employee-summary-report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping(value = "/employee-summary/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportEmployeeSummaryPdf(
            @RequestParam(required = false) Integer departmentId) throws IOException, DocumentException {
        byte[] data = reportExportService.exportEmployeeSummaryToPdf(departmentId);
        return buildFileResponse(data, "employee-summary-report.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    private ResponseEntity<byte[]> buildFileResponse(byte[] data, String filename, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(data.length);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
