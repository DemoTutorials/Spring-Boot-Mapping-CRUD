package com.employee.service;

import com.employee.dto.DepartmentRequestDTO;
import com.employee.dto.DepartmentResponseDTO;
import com.employee.projection.DepartmentProjection;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    DepartmentResponseDTO create(@Valid DepartmentRequestDTO dto);

    DepartmentResponseDTO getById(Long id);

    List<DepartmentResponseDTO> getAll();

    DepartmentResponseDTO update(Long id, @Valid DepartmentRequestDTO dto);

    void delete(Long id);

    DepartmentResponseDTO patchUpdate(Long id, Map<String, Object> updates);

    List<DepartmentResponseDTO> getByLocation(String location);

    List<DepartmentProjection> getWithEmployeeCounts();

    List<DepartmentResponseDTO> getWithAssociations();
}
