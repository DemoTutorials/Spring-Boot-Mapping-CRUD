package com.employee.controller;

import com.employee.dto.DesignationRequestDTO;
import com.employee.dto.DesignationResponseDTO;
import com.employee.projection.DesignationProjection;
import com.employee.service.DesignationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/designations")
public class DesignationController {
    private final DesignationService designationService;

    public DesignationController(DesignationService designationService) {
        this.designationService = designationService;
    }

    @PostMapping
    public ResponseEntity<DesignationResponseDTO> create(@Valid @RequestBody DesignationRequestDTO dto) {
        return new ResponseEntity<>(designationService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignationResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(designationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<DesignationResponseDTO>> getAll() {
        return ResponseEntity.ok(designationService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignationResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DesignationRequestDTO dto) {
        return ResponseEntity.ok(designationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        designationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-counts")
    public ResponseEntity<List<DesignationProjection>> getWithEmployeeCounts() {
        return ResponseEntity.ok(designationService.getWithEmployeeCounts());
    }

    @GetMapping("/with-employees")
    public ResponseEntity<List<DesignationResponseDTO>> getWithEmployees() {
        return ResponseEntity.ok(designationService.getWithEmployees());
    }
}
