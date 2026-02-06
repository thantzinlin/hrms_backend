package com.hrms.service;

import com.hrms.dto.CreatePositionRequest;
import com.hrms.dto.PositionDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Position;
import com.hrms.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Transactional
    public PositionDto create(CreatePositionRequest request) {
        Position position = new Position();
        position.setPositionName(request.getPositionName());
        position.setDescription(request.getDescription());
        position.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        Position saved = positionRepository.save(position);
        return mapToDto(saved);
    }

    public List<PositionDto> getAll() {
        return positionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PositionDto getById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));
        return mapToDto(position);
    }

    @Transactional
    public PositionDto update(Long id, CreatePositionRequest request) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));
        position.setPositionName(request.getPositionName());
        position.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            position.setIsActive(request.getIsActive());
        }
        Position updated = positionRepository.save(position);
        return mapToDto(updated);
    }

    public void delete(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }

    private PositionDto mapToDto(Position position) {
        PositionDto dto = new PositionDto();
        dto.setPositionId(position.getPositionId());
        dto.setPositionName(position.getPositionName());
        dto.setDescription(position.getDescription());
        dto.setIsActive(position.getIsActive());
        return dto;
    }
}
