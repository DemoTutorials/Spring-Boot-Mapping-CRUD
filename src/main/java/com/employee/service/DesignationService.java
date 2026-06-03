package com.employee.service;

import com.employee.dto.DesignationRequestDTO;
import com.employee.dto.DesignationResponseDTO;
import com.employee.projection.DesignationProjection;

import java.util.List;

public interface DesignationService {
    DesignationResponseDTO create(DesignationRequestDTO dto);
    DesignationResponseDTO getById(Long id);
    List<DesignationResponseDTO> getAll();
    DesignationResponseDTO update(Long id, DesignationRequestDTO dto);
    void delete(Long id);

    List<DesignationProjection> getWithEmployeeCounts();
    List<DesignationResponseDTO> getWithEmployees();
}
