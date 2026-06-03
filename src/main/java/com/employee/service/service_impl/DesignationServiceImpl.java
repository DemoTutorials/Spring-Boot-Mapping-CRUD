package com.employee.service.service_impl;

import com.employee.dto.DesignationRequestDTO;
import com.employee.dto.DesignationResponseDTO;
import com.employee.entity.Designation;
import com.employee.exception.custom_exception.ResourceNotFoundException;
import com.employee.projection.DesignationProjection;
import com.employee.repository.DesignationRepository;
import com.employee.service.DesignationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DesignationServiceImpl implements DesignationService {
    private final DesignationRepository designationRepository;
    private final ModelMapper modelMapper;

    public DesignationServiceImpl(DesignationRepository designationRepository, ModelMapper modelMapper) {
        this.designationRepository = designationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public DesignationResponseDTO create(DesignationRequestDTO dto) {
        if (designationRepository.existsByDesignationNameIgnoreCase(dto.getDesignationName())) {
            throw new IllegalArgumentException("Designation name already exists: " + dto.getDesignationName());
        }

        Designation designation = modelMapper.map(dto, Designation.class);
        Designation saved = designationRepository.save(designation);
        return modelMapper.map(saved, DesignationResponseDTO.class);
    }

    @Override
    public DesignationResponseDTO getById(Long id) {
        Designation des = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation"+ id));
        return modelMapper.map(des, DesignationResponseDTO.class);
    }

    @Override
    public List<DesignationResponseDTO> getAll() {
        return designationRepository.findAll().stream()
                .map(des -> modelMapper.map(des, DesignationResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DesignationResponseDTO update(Long id, DesignationRequestDTO dto) {
        Designation des = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation"+ id));

        modelMapper.map(dto, des);
        Designation updated = designationRepository.save(des);
        return modelMapper.map(updated, DesignationResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Designation des = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation"+ id));

        // Clear associations
        des.getEmployees().forEach(emp -> emp.setDesignation(null));
        des.getDepartments().forEach(dept -> dept.getDesignations().remove(des));

        designationRepository.delete(des);
    }

    @Override
    public List<DesignationProjection> getWithEmployeeCounts() {
        return designationRepository.getDesignationEmployeeCounts();
    }

    @Override
    public List<DesignationResponseDTO> getWithEmployees() {
        List<Designation> desigs = designationRepository.findAll();
        return desigs.stream()
                .map(des -> modelMapper.map(des, DesignationResponseDTO.class))
                .collect(Collectors.toList());
    }
}
