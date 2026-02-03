package com.hrms.service;

import com.hrms.dto.CreateHolidayRequest;
import com.hrms.dto.HolidayDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Holiday;
import com.hrms.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Transactional
    public HolidayDto createHoliday(CreateHolidayRequest request) {
        Holiday holiday = new Holiday();
        holiday.setName(request.getName());
        holiday.setDate(request.getDate());
        Holiday savedHoliday = holidayRepository.save(holiday);
        return mapToDto(savedHoliday);
    }

    public List<HolidayDto> getAllHolidays() {
        return holidayRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public HolidayDto getHolidayById(Integer id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with id: " + id));
        return mapToDto(holiday);
    }

    @Transactional
    public HolidayDto updateHoliday(Integer id, CreateHolidayRequest request) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with id: " + id));
        holiday.setName(request.getName());
        holiday.setDate(request.getDate());
        Holiday updatedHoliday = holidayRepository.save(holiday);
        return mapToDto(updatedHoliday);
    }

    public void deleteHoliday(Integer id) {
        if (!holidayRepository.existsById(id)) {
            throw new ResourceNotFoundException("Holiday not found with id: " + id);
        }
        holidayRepository.deleteById(id);
    }

    private HolidayDto mapToDto(Holiday holiday) {
        HolidayDto dto = new HolidayDto();
        dto.setId(holiday.getId());
        dto.setName(holiday.getName());
        dto.setDate(holiday.getDate());
        return dto;
    }
}
