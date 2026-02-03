package com.hrms.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateOvertimeRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    @FutureOrPresent
    private LocalDate date;

    @NotNull
    @Positive
    private Double hours;

    private String reason;
}
