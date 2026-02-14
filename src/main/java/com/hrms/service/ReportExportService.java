package com.hrms.service;

import com.hrms.dto.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private ReportService reportService;

    public byte[] exportAttendanceToExcel(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws IOException {
        List<AttendanceReportRowDto> data = reportService.getAttendanceReport(startDate, endDate, departmentId);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Attendance Report");
            createAttendanceExcelHeader(sheet);
            int rowNum = 1;
            for (AttendanceReportRowDto r : data) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, r.getEmployeeId());
                setCell(row, 1, r.getEmployeeName());
                setCell(row, 2, r.getDepartmentName());
                setCell(row, 3, r.getDate() != null ? r.getDate().format(DATE_FORMAT) : "");
                setCell(row, 4, r.getCheckInTime() != null ? r.getCheckInTime().format(DATETIME_FORMAT) : "");
                setCell(row, 5, r.getCheckOutTime() != null ? r.getCheckOutTime().format(DATETIME_FORMAT) : "");
                setCell(row, 6, r.getWorkedHours());
            }
            autoSizeColumns(sheet, 7);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportAttendanceToPdf(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws DocumentException, IOException {
        List<AttendanceReportRowDto> data = reportService.getAttendanceReport(startDate, endDate, departmentId);
        return createPdfReport("Attendance Report", new String[]{"Employee ID", "Name", "Department", "Date", "Check In", "Check Out", "Hours"},
                data.stream().map(r -> new String[]{
                        str(r.getEmployeeId()),
                        str(r.getEmployeeName()),
                        str(r.getDepartmentName()),
                        r.getDate() != null ? r.getDate().format(DATE_FORMAT) : "",
                        r.getCheckInTime() != null ? r.getCheckInTime().format(DATETIME_FORMAT) : "",
                        r.getCheckOutTime() != null ? r.getCheckOutTime().format(DATETIME_FORMAT) : "",
                        r.getWorkedHours() != null ? String.format("%.1f", r.getWorkedHours()) : ""
                }).toList());
    }

    public byte[] exportLeaveToExcel(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws IOException {
        List<LeaveReportRowDto> data = reportService.getLeaveReport(startDate, endDate, departmentId);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Leave Report");
            createLeaveExcelHeader(sheet);
            int rowNum = 1;
            for (LeaveReportRowDto r : data) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, r.getEmployeeId());
                setCell(row, 1, r.getEmployeeName());
                setCell(row, 2, r.getDepartmentName());
                setCell(row, 3, r.getLeaveTypeName());
                setCell(row, 4, r.getStartDate() != null ? r.getStartDate().format(DATE_FORMAT) : "");
                setCell(row, 5, r.getEndDate() != null ? r.getEndDate().format(DATE_FORMAT) : "");
                setCell(row, 6, r.getStatus());
                setCell(row, 7, r.getReason());
            }
            autoSizeColumns(sheet, 8);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportLeaveToPdf(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws DocumentException, IOException {
        List<LeaveReportRowDto> data = reportService.getLeaveReport(startDate, endDate, departmentId);
        return createPdfReport("Leave Report", new String[]{"Employee ID", "Name", "Department", "Leave Type", "From", "To", "Status", "Reason"},
                data.stream().map(r -> new String[]{
                        str(r.getEmployeeId()),
                        str(r.getEmployeeName()),
                        str(r.getDepartmentName()),
                        str(r.getLeaveTypeName()),
                        r.getStartDate() != null ? r.getStartDate().format(DATE_FORMAT) : "",
                        r.getEndDate() != null ? r.getEndDate().format(DATE_FORMAT) : "",
                        str(r.getStatus()),
                        str(r.getReason())
                }).toList());
    }

    public byte[] exportOvertimeToExcel(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws IOException {
        List<OvertimeReportRowDto> data = reportService.getOvertimeReport(startDate, endDate, departmentId);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Overtime Report");
            createOvertimeExcelHeader(sheet);
            int rowNum = 1;
            for (OvertimeReportRowDto r : data) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, r.getEmployeeId());
                setCell(row, 1, r.getEmployeeName());
                setCell(row, 2, r.getDepartmentName());
                setCell(row, 3, r.getDate() != null ? r.getDate().format(DATE_FORMAT) : "");
                setCell(row, 4, r.getHours());
                setCell(row, 5, r.getStatus());
                setCell(row, 6, r.getReason());
            }
            autoSizeColumns(sheet, 7);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportOvertimeToPdf(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws DocumentException, IOException {
        List<OvertimeReportRowDto> data = reportService.getOvertimeReport(startDate, endDate, departmentId);
        return createPdfReport("Overtime Report", new String[]{"Employee ID", "Name", "Department", "Date", "Hours", "Status", "Reason"},
                data.stream().map(r -> new String[]{
                        str(r.getEmployeeId()),
                        str(r.getEmployeeName()),
                        str(r.getDepartmentName()),
                        r.getDate() != null ? r.getDate().format(DATE_FORMAT) : "",
                        r.getHours() != null ? String.valueOf(r.getHours()) : "",
                        str(r.getStatus()),
                        str(r.getReason())
                }).toList());
    }

    public byte[] exportClaimToExcel(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws IOException {
        List<ClaimReportRowDto> data = reportService.getClaimReport(startDate, endDate, departmentId);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Claim Report");
            createClaimExcelHeader(sheet);
            int rowNum = 1;
            for (ClaimReportRowDto r : data) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, r.getClaimNumber());
                setCell(row, 1, r.getEmployeeId());
                setCell(row, 2, r.getEmployeeName());
                setCell(row, 3, r.getDepartmentName());
                setCell(row, 4, r.getClaimTypeName());
                setCell(row, 5, r.getClaimDate() != null ? r.getClaimDate().format(DATE_FORMAT) : "");
                setCell(row, 6, r.getTotalAmount());
                setCell(row, 7, r.getCurrency());
                setCell(row, 8, r.getStatus());
            }
            autoSizeColumns(sheet, 9);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportClaimToPdf(java.time.LocalDate startDate, java.time.LocalDate endDate, Integer departmentId) throws DocumentException, IOException {
        List<ClaimReportRowDto> data = reportService.getClaimReport(startDate, endDate, departmentId);
        return createPdfReport("Claim Report", new String[]{"Claim #", "Employee ID", "Name", "Department", "Type", "Date", "Amount", "Currency", "Status"},
                data.stream().map(r -> new String[]{
                        str(r.getClaimNumber()),
                        str(r.getEmployeeId()),
                        str(r.getEmployeeName()),
                        str(r.getDepartmentName()),
                        str(r.getClaimTypeName()),
                        r.getClaimDate() != null ? r.getClaimDate().format(DATE_FORMAT) : "",
                        r.getTotalAmount() != null ? r.getTotalAmount().toPlainString() : "",
                        str(r.getCurrency()),
                        str(r.getStatus())
                }).toList());
    }

    public byte[] exportEmployeeSummaryToExcel(Integer departmentId) throws IOException {
        List<EmployeeSummaryRowDto> data = reportService.getEmployeeSummaryReport(departmentId);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employee Summary");
            createEmployeeSummaryExcelHeader(sheet);
            int rowNum = 1;
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (EmployeeSummaryRowDto r : data) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, r.getEmployeeCode());
                setCell(row, 1, r.getEmployeeName());
                setCell(row, 2, r.getEmail());
                setCell(row, 3, r.getDepartmentName());
                setCell(row, 4, r.getPositionName());
                setCell(row, 5, r.getStatus());
                setCell(row, 6, r.getJoinDate() != null ? r.getJoinDate().format(dtFormat) : "");
            }
            autoSizeColumns(sheet, 7);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportEmployeeSummaryToPdf(Integer departmentId) throws DocumentException, IOException {
        List<EmployeeSummaryRowDto> data = reportService.getEmployeeSummaryReport(departmentId);
        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return createPdfReport("Employee Summary Report", new String[]{"Employee ID", "Name", "Email", "Department", "Position", "Status", "Join Date"},
                data.stream().map(r -> new String[]{
                        str(r.getEmployeeCode()),
                        str(r.getEmployeeName()),
                        str(r.getEmail()),
                        str(r.getDepartmentName()),
                        str(r.getPositionName()),
                        str(r.getStatus()),
                        r.getJoinDate() != null ? r.getJoinDate().format(dtFormat) : ""
                }).toList());
    }

    // --- Excel helpers ---
    private void createAttendanceExcelHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] cols = {"Employee ID", "Name", "Department", "Date", "Check In", "Check Out", "Hours"};
        for (int i = 0; i < cols.length; i++) setCell(header, i, cols[i]);
    }

    private void createLeaveExcelHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] cols = {"Employee ID", "Name", "Department", "Leave Type", "From", "To", "Status", "Reason"};
        for (int i = 0; i < cols.length; i++) setCell(header, i, cols[i]);
    }

    private void createOvertimeExcelHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] cols = {"Employee ID", "Name", "Department", "Date", "Hours", "Status", "Reason"};
        for (int i = 0; i < cols.length; i++) setCell(header, i, cols[i]);
    }

    private void createClaimExcelHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] cols = {"Claim #", "Employee ID", "Name", "Department", "Type", "Date", "Amount", "Currency", "Status"};
        for (int i = 0; i < cols.length; i++) setCell(header, i, cols[i]);
    }

    private void createEmployeeSummaryExcelHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] cols = {"Employee ID", "Name", "Email", "Department", "Position", "Status", "Join Date"};
        for (int i = 0; i < cols.length; i++) setCell(header, i, cols[i]);
    }

    private void setCell(Row row, int col, String value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
    }

    private void setCell(Row row, int col, Number value) {
        Cell cell = row.createCell(col);
        if (value != null) cell.setCellValue(value.doubleValue());
        else cell.setCellValue("");
    }

    private void setCell(Row row, int col, BigDecimal value) {
        Cell cell = row.createCell(col);
        if (value != null) cell.setCellValue(value.doubleValue());
        else cell.setCellValue("");
    }

    private void autoSizeColumns(Sheet sheet, int colCount) {
        for (int i = 0; i < colCount; i++) sheet.autoSizeColumn(i);
    }

    // --- PDF helpers ---
    private byte[] createPdfReport(String title, String[] headers, List<String[]> rows) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph p = new Paragraph(title, titleFont);
        p.setSpacingAfter(12);
        document.add(p);

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 7);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setPadding(4);
            table.addCell(cell);
        }

        for (String[] row : rows) {
            for (String v : row) {
                PdfPCell cell = new PdfPCell(new Phrase(str(v), cellFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPadding(3);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private static String str(Object o) {
        return o != null ? String.valueOf(o) : "";
    }
}
