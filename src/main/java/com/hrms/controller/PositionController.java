package com.hrms.controller;

import com.hrms.dto.CreatePositionRequest;
import com.hrms.dto.PositionDto;
import com.hrms.service.PositionService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<PositionDto>> create(
            @Valid @RequestBody CreatePositionRequest request) {
        PositionDto created = positionService.create(request);
        return new ResponseEntity<>(CustomApiResponse.<PositionDto>builder().data(created).build(),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<PositionDto>>> getAll() {
        List<PositionDto> positions = positionService.getAll();
        return ResponseEntity.ok(CustomApiResponse.<List<PositionDto>>builder().data(positions).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PositionDto>> getById(@PathVariable Long id) {
        PositionDto position = positionService.getById(id);
        return ResponseEntity.ok(CustomApiResponse.<PositionDto>builder().data(position).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PositionDto>> update(@PathVariable Long id,
            @Valid @RequestBody CreatePositionRequest request) {
        PositionDto updated = positionService.update(id, request);
        return ResponseEntity.ok(CustomApiResponse.<PositionDto>builder().data(updated).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> delete(@PathVariable Long id) {
        positionService.delete(id);
        return ResponseEntity
                .ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }
}
