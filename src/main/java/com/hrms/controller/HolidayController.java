package com.hrms.controller;

import com.hrms.dto.HolidayDto;
import com.hrms.service.HolidayService;
import com.hrms.util.CustomApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only holidays endpoint for all authenticated employees.
 * Admin CRUD remains at /api/admin/holidays.
 */
@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<HolidayDto>>> getHolidays() {
        List<HolidayDto> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(CustomApiResponse.<List<HolidayDto>>builder().data(holidays).build());
    }
}
