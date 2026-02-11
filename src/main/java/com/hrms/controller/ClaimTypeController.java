package com.hrms.controller;

import com.hrms.dto.CreateClaimTypeRequest;
import com.hrms.dto.ClaimTypeDto;
import com.hrms.service.ClaimTypeService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-types")
public class ClaimTypeController {

    @Autowired
    private ClaimTypeService claimTypeService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<ClaimTypeDto>> create(
            @Valid @RequestBody CreateClaimTypeRequest request) {
        ClaimTypeDto created = claimTypeService.create(request);
        return new ResponseEntity<>(CustomApiResponse.<ClaimTypeDto>builder().data(created).build(), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        if (page != null && size != null) {
            Sort order = sort != null && !sort.isBlank()
                    ? parseSort(sort)
                    : Sort.by("name").ascending();
            Pageable pageable = PageRequest.of(page, size, order);
            Page<ClaimTypeDto> result = claimTypeService.getPage(pageable);
            return ResponseEntity.ok(CustomApiResponse.<Page<ClaimTypeDto>>builder().data(result).build());
        }
        List<ClaimTypeDto> types = claimTypeService.getAll();
        return ResponseEntity.ok(CustomApiResponse.<List<ClaimTypeDto>>builder().data(types).build());
    }

    @GetMapping("/active")
    public ResponseEntity<CustomApiResponse<List<ClaimTypeDto>>> getActive() {
        List<ClaimTypeDto> types = claimTypeService.getActive();
        return ResponseEntity.ok(CustomApiResponse.<List<ClaimTypeDto>>builder().data(types).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<ClaimTypeDto>> getById(@PathVariable Long id) {
        ClaimTypeDto dto = claimTypeService.getById(id);
        return ResponseEntity.ok(CustomApiResponse.<ClaimTypeDto>builder().data(dto).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<ClaimTypeDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateClaimTypeRequest request) {
        ClaimTypeDto updated = claimTypeService.update(id, request);
        return ResponseEntity.ok(CustomApiResponse.<ClaimTypeDto>builder().data(updated).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> delete(@PathVariable Long id) {
        claimTypeService.delete(id);
        return ResponseEntity.ok(CustomApiResponse.<Void>builder().returnMessage("Deleted successfully").build());
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, property);
    }
}
