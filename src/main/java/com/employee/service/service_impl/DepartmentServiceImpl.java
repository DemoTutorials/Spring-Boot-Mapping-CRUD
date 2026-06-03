package com.employee.service.service_impl;

import com.employee.dto.DepartmentRequestDTO;
import com.employee.dto.DepartmentResponseDTO;
import com.employee.entity.Department;
import com.employee.entity.Designation;
import com.employee.entity.Employee;
import com.employee.exception.custom_exception.DepartmentAlreadyExistsException;
import com.employee.exception.custom_exception.ResourceNotFoundException;
import com.employee.projection.DepartmentProjection;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.DesignationRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.service.DepartmentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DesignationRepository designationRepository;
    private final ModelMapper modelMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, DesignationRepository designationRepository, ModelMapper modelMapper) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.designationRepository = designationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public DepartmentResponseDTO create(DepartmentRequestDTO dto) {
        if (departmentRepository.existsByDepartmentNameIgnoreCase(dto.getDepartmentName())) {
            throw new DepartmentAlreadyExistsException("Department already exists: " + dto.getDepartmentName());
        }

        Department department = modelMapper.map(dto, Department.class);

        // Handle ManyToMany Designations
        if (dto.getDesignationIds() != null && !dto.getDesignationIds().isEmpty()) {
            List<Designation> designations = designationRepository.findAllByIdIn(dto.getDesignationIds());
            if (designations.size() != dto.getDesignationIds().size()) {
                throw new ResourceNotFoundException("Some designations not found");
            }
            department.setDesignations(designations);
            // Maintain bidirectional
            designations.forEach(desig -> desig.getDepartments().add(department));
        }

        // Handle OneToOne Head (Employee)
        if (dto.getHeadEmployeeId() != null) {
            Employee head = employeeRepository.findByIdAndIsDeletedFalse(dto.getHeadEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found!..."));
            department.setHead(head);
            head.setHeadOfDepartment(department); // Bi-directional
        }

        Department saved = departmentRepository.save(department);
        return modelMapper.map(saved, DepartmentResponseDTO.class);
    }

    @Override
    public DepartmentResponseDTO getById(Long id) {
        Department dept = departmentRepository.findByIdWithAssociations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department ID Not Found!..."));
        return modelMapper.map(dept, DepartmentResponseDTO.class);
    }

    @Override
    public List<DepartmentResponseDTO> getAll() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(dept -> modelMapper.map(dept, DepartmentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentResponseDTO update(Long id, DepartmentRequestDTO dto) {
        Department dept = departmentRepository.findByIdWithAssociations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department ID Not Found!..."));

        modelMapper.map(dto, dept);

        // Update ManyToMany Designations
        if (dto.getDesignationIds() != null) {
            List<Designation> designations = designationRepository.findAllByIdIn(dto.getDesignationIds());
            dept.getDesignations().clear();
            dept.setDesignations(designations);
            designations.forEach(desig -> desig.getDepartments().add(dept));
        }

        // Update OneToOne Head
        if (dto.getHeadEmployeeId() != null) {
            Employee head = employeeRepository.findByIdAndIsDeletedFalse(dto.getHeadEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found!..."));
            dept.setHead(head);
            head.setHeadOfDepartment(dept);
        }

        Department updated = departmentRepository.save(dept);
        return modelMapper.map(updated, DepartmentResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department ID Not Found!..."));

        // Soft-delete associated employees if needed (optional)
        dept.getEmployees().forEach(emp -> {
            emp.setIsDeleted(true);
            emp.setIsActive(false);
        });

        // Remove associations
        dept.getDesignations().forEach(desig -> desig.getDepartments().remove(dept));
        dept.getDesignations().clear();

        if (dept.getHead() != null) {
            dept.getHead().setHeadOfDepartment(null);
        }

        departmentRepository.delete(dept); // Or soft-delete if you add isDeleted to Department
    }

    @Override
    @Transactional
    public DepartmentResponseDTO patchUpdate(Long id, Map<String, Object> updates) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department ID Not Found!..."));

        // Dynamic patch logic (similar to your Employee patch)
        updates.forEach((field, value) -> {
            switch (field) {
                case "departmentName" -> dept.setDepartmentName((String) value);
                case "location" -> dept.setLocation((String) value);
                case "description" -> dept.setDescription((String) value);
                // Add more fields as needed
                default -> throw new IllegalArgumentException("Unsupported field: " + field);
            }
        });

        Department updated = departmentRepository.save(dept);
        return modelMapper.map(updated, DepartmentResponseDTO.class);
    }

    @Override
    public List<DepartmentResponseDTO> getByLocation(String location) {
        List<Department> depts = departmentRepository.findByLocationContaining(location);
        return depts.stream()
                .map(dept -> modelMapper.map(dept, DepartmentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentProjection> getWithEmployeeCounts() {
        return departmentRepository.getDepartmentEmployeeCounts();
    }

    @Override
    public List<DepartmentResponseDTO> getWithAssociations() {
        List<Department> depts = departmentRepository.findAll(); // Or custom query
        return depts.stream()
                .map(dept -> modelMapper.map(dept, DepartmentResponseDTO.class))
                .collect(Collectors.toList());
    }
}
