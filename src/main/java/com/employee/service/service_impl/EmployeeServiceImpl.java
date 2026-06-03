package com.employee.service.service_impl;

import com.employee.dto.EmployeeRequestDTO;
import com.employee.dto.EmployeeResponseDTO;
import com.employee.entity.Department;
import com.employee.entity.Designation;
import com.employee.entity.Employee;
import com.employee.enums.BloodGroup;
import com.employee.exception.custom_exception.EmployeeAlreadyExistsException;
import com.employee.exception.custom_exception.EmployeeNotFoundException;
import com.employee.exception.custom_exception.ResourceNotFoundException;
import com.employee.projection.EmployeeBasicProjection;
import com.employee.projection.EmployeeWithDepartmentProjection;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.DesignationRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final String EMPLOYEE_NOT_FOUND_MSG = "Employee Not Found with ID: ";
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, DesignationRepository designationRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public EmployeeResponseDTO create(EmployeeRequestDTO employeeRequestDTO) {
        if (employeeRepository.existsByEmpEmailAndIsDeletedFalse(employeeRequestDTO.getEmpEmail())) {
            throw new EmployeeAlreadyExistsException("Email already exists: " + employeeRequestDTO.getEmpEmail());
        }
        if (employeeRepository.existsByContactNoAndIsDeletedFalse(employeeRequestDTO.getContactNo())) {
            throw new EmployeeAlreadyExistsException("Contact already exists: " + employeeRequestDTO.getContactNo());
        }

        // Create Employee and map only safe fields
        Employee employee = new Employee();
        employee.setEmpName(employeeRequestDTO.getEmpName());
        employee.setEmpEmail(employeeRequestDTO.getEmpEmail());
        employee.setContactNo(employeeRequestDTO.getContactNo());
        employee.setAddress(employeeRequestDTO.getAddress());
        employee.setSalary(employeeRequestDTO.getSalary());
        employee.setBirthDate(employeeRequestDTO.getBirthDate());
        employee.setBloodGroup(employeeRequestDTO.getBloodGroup());
     //   modelMapper.map(employeeRequestDTO, employee);           // maps name, email, salary, birthDate, etc.

        // Manually set associations (prevents ambiguity)
        Department dept = departmentRepository.findById(employeeRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeRequestDTO.getDepartmentId()));

        Designation desig = designationRepository.findById(employeeRequestDTO.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + employeeRequestDTO.getDesignationId()));

        employee.setDepartment(dept);
        employee.setDesignation(desig);

        // Maintain bidirectional relationship
        dept.getEmployees().add(employee);
        desig.getEmployees().add(employee);

        employee.setIsActive(true);
        employee.setIsDeleted(false);

        Employee savedEmployee = employeeRepository.save(employee);
        return modelMapper.map(savedEmployee, EmployeeResponseDTO.class);
    }

    @Override
    public List<EmployeeResponseDTO> getAll() {
        List<Employee> employees = employeeRepository.findAllByIsDeletedFalse();
        if(employees.isEmpty()){
            throw new EmployeeNotFoundException("Employees Not Found!...");
        }
        return employees.stream().map(employee -> modelMapper.map(employee, EmployeeResponseDTO.class)).toList();
    }

    @Override
    public EmployeeResponseDTO getById(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartmentAndDesignation(id).orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG + id));
        return modelMapper.map(employee, EmployeeResponseDTO.class);
    }

    @Override
    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG + id));
        modelMapper.map(employeeRequestDTO,employee);
        // Update Associations if provided
        if (employeeRequestDTO.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(employeeRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department Not Found!..."));
            employee.setDepartment(dept);
            dept.getEmployees().add(employee);
        }
        if (employeeRequestDTO.getDesignationId() != null) {
            Designation desig = designationRepository.findById(employeeRequestDTO.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation Not Found!..."));
            employee.setDesignation(desig);
            desig.getEmployees().add(employee);
        }
        Employee updatedEmployee = employeeRepository.save(employee);
        return modelMapper.map(updatedEmployee, EmployeeResponseDTO.class);
    }

    @Override
    public void delete(Long id) {
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG + id));
        employee.setIsActive(false);
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    public EmployeeResponseDTO patchUpdate(Long id, Map<String, Object> updates) {
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG + id));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        updates.forEach((field,value)->{
            try {
                updateField(employee, field, value, dateFormatter);
            } catch (IllegalArgumentException e) {
                throw e;                    // rethrow known validation errors
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to update field '" + field + "': " + e.getMessage(), e);
            }
        });
        Employee updatedEmployee = employeeRepository.save(employee);
        return modelMapper.map(updatedEmployee, EmployeeResponseDTO.class);
    }

    private void updateField(Employee employee, String field, Object value, DateTimeFormatter fmt) {
        switch (field) {
            case "empName" -> employee.setEmpName((String) value);
            case "empEmail" -> employee.setEmpEmail((String) value);
            case "contactNo" -> employee.setContactNo((String) value);
            case "address" -> employee.setAddress((String) value);

            case "salary"     -> setSalary(employee, value);
            case "birthDate"  -> setBirthDate(employee, value, fmt);
            case "bloodGroup" -> setBloodGroup(employee, value);

            case "departmentId" -> {
                Department dept = departmentRepository.findById(Long.valueOf(value.toString()))
                        .orElseThrow(() -> new ResourceNotFoundException("Department Not Found!..."));
                employee.setDepartment(dept);
            }
            case "designationId" -> {
                Designation desig = designationRepository.findById(Long.valueOf(value.toString()))
                        .orElseThrow(() -> new ResourceNotFoundException("Designation Not Found!..."));
                employee.setDesignation(desig);
            }

            default -> throw new IllegalArgumentException("Field is not supported: " + field);
        }
    }

    private void setSalary(Employee employee, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Salary cannot be null");
        }

        if (value instanceof Number num) {
            employee.setSalary(BigDecimal.valueOf(num.doubleValue()));
            return;
        }

        if (value instanceof String str) {
            try {
                employee.setSalary(new BigDecimal(str.trim()));
                return;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid salary format: " + str);
            }
        }

        throw new IllegalArgumentException("Salary must be number or numeric string, got: " + value.getClass().getSimpleName());
    }

    private void setBirthDate(Employee employee, Object value, DateTimeFormatter fmt) {
        if (value instanceof String str) {
            try {
                employee.setBirthDate(LocalDate.parse(str.trim(), fmt));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid birthDate format. Expected dd-MMM-yyyy, got: " + str);
            }
        } else {
            throw new IllegalArgumentException("birthDate must be a string in format dd-MMM-yyyy");
        }
    }

    private void setBloodGroup(Employee employee, Object value) {
        if (value instanceof BloodGroup bg) {
            employee.setBloodGroup(bg);
        } else if (value instanceof String str) {
            employee.setBloodGroup(BloodGroup.fromString(str.trim()));
        } else {
            throw new IllegalArgumentException(
                    "bloodGroup must be BloodGroup enum or valid string, got: " +
                            (value != null ? value.getClass().getSimpleName() : "null")
            );
        }
    }

    @Override
    public List<EmployeeResponseDTO> getByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentIdAndIsDeletedFalse(departmentId).stream()
                .map(emp -> modelMapper.map(emp, EmployeeResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponseDTO> getByDesignation(Long designationId) {
        return employeeRepository.findByDesignationIdAndIsDeletedFalse(designationId).stream()
                .map(emp -> modelMapper.map(emp, EmployeeResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeBasicProjection> getAllBasicProjections() {
        return employeeRepository.findAllBasicProjections();
    }

    @Override
    public List<EmployeeWithDepartmentProjection> getAllWithDepartmentAndDesignation() {
        return employeeRepository.findAllWithDepartmentAndDesignation();
    }
}
