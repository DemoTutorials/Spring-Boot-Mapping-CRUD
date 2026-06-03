package com.employee.controller;

import com.employee.dto.DepartmentRequestDTO;
import com.employee.dto.DepartmentResponseDTO;
import com.employee.projection.DepartmentProjection;
import com.employee.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> create(@Valid @RequestBody DepartmentRequestDTO dto) {
        return new ResponseEntity<>(departmentService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequestDTO dto) {
        return ResponseEntity.ok(departmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> patchUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(departmentService.patchUpdate(id, updates));
    }

    // Advanced Endpoints
    @GetMapping("/location")
    public ResponseEntity<List<DepartmentResponseDTO>> getByLocation(@RequestParam String location) {
        return ResponseEntity.ok(departmentService.getByLocation(location));
    }

    @GetMapping("/with-counts")
    public ResponseEntity<List<DepartmentProjection>> getWithEmployeeCounts() {
        return ResponseEntity.ok(departmentService.getWithEmployeeCounts());
    }

    @GetMapping("/with-associations")
    public ResponseEntity<List<DepartmentResponseDTO>> getWithAssociations() {
        return ResponseEntity.ok(departmentService.getWithAssociations());
    }
}
