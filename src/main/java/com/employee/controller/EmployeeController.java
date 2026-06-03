package com.employee.controller;

import com.employee.dto.EmployeeRequestDTO;
import com.employee.dto.EmployeeResponseDTO;
import com.employee.projection.EmployeeBasicProjection;
import com.employee.projection.EmployeeWithDepartmentProjection;
import com.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> save(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(employeeRequestDTO));
    }

    // GET ALL Employees
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getAll());
    }

    // GET By ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getById(id));
    }

    // UPDATE By ID
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id,@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.update(id,employeeRequestDTO));
    }

    // DELETE By ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Specific Fields Update By ID
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> patchUpdate(@PathVariable Long id, @RequestBody Map<String,Object> updates){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.patchUpdate(id,updates));
    }

    // Association Endpoints
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeResponseDTO>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(employeeService.getByDepartment(departmentId));
    }

    @GetMapping("/designation/{designationId}")
    public ResponseEntity<List<EmployeeResponseDTO>> getByDesignation(@PathVariable Long designationId) {
        return ResponseEntity.ok(employeeService.getByDesignation(designationId));
    }

    // Projection Endpoints
    @GetMapping("/projections/basic")
    public ResponseEntity<List<EmployeeBasicProjection>> getBasicProjections() {
        return ResponseEntity.ok(employeeService.getAllBasicProjections());
    }

    @GetMapping("/projections/full")
    public ResponseEntity<List<EmployeeWithDepartmentProjection>> getFullProjections() {
        return ResponseEntity.ok(employeeService.getAllWithDepartmentAndDesignation());
    }
}
