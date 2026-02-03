package com.hrms.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateHolidayRequest {
    @NotBlank
    private String name;

    @NotNull
    @FutureOrPresent
    private LocalDate date;
}
