package com.employee.model_mapper;

import com.employee.dto.DepartmentResponseDTO;
import com.employee.dto.DesignationResponseDTO;
import com.employee.dto.EmployeeResponseDTO;
import com.employee.entity.Department;
import com.employee.entity.Designation;
import com.employee.entity.Employee;
import com.employee.projection.DepartmentProjection;
import com.employee.projection.DesignationProjection;
import com.employee.projection.EmployeeBasicProjection;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        // 1. Department → DepartmentProjection (your existing - keep as-is)
        modelMapper.typeMap(Department.class, DepartmentProjection.class)
                .addMappings(mapper ->
                        mapper.using(ctx -> {
                            @SuppressWarnings("unchecked")
                            List<Employee> list = (List<Employee>) ctx.getSource();
                            return list != null ? Long.valueOf(list.size()) : 0L;
                        }).map(Department::getEmployees, DepartmentProjection::setEmployeeCount)
                );

        // 2. Designation → DesignationProjection (same)
        modelMapper.typeMap(Designation.class, DesignationProjection.class)
                .addMappings(mapper ->
                        mapper.using(ctx -> {
                            @SuppressWarnings("unchecked")
                            List<Employee> list = (List<Employee>) ctx.getSource();
                            return list != null ? Long.valueOf(list.size()) : 0L;
                        }).map(Designation::getEmployees, DesignationProjection::setEmployeeCount)
                );

        // 3. Employee → EmployeeBasicProjection (keep)
        modelMapper.typeMap(Employee.class, EmployeeBasicProjection.class)
                .addMappings(mapper -> {
                    mapper.map(Employee::getId, EmployeeBasicProjection::setId);
                    mapper.map(Employee::getEmpName, EmployeeBasicProjection::setEmpName);
                    mapper.map(Employee::getEmpEmail, EmployeeBasicProjection::setEmpEmail);
                });

        // 4. Key Fix: Employee → EmployeeResponseDTO
        // Create EMPTY type map first → no implicit mappings yet
        TypeMap<Employee, EmployeeResponseDTO> employeeToDtoMap = modelMapper.emptyTypeMap(Employee.class, EmployeeResponseDTO.class);

        // Then add your explicit mappings + skips
        employeeToDtoMap.addMappings(mapper -> {
            // Explicit simple fields (adjust getters if entity uses different names like getName() vs getEmpName())
            mapper.map(Employee::getId, EmployeeResponseDTO::setId);
            mapper.map(Employee::getEmpName, EmployeeResponseDTO::setEmpName);
            mapper.map(Employee::getEmpEmail, EmployeeResponseDTO::setEmpEmail);
            mapper.map(Employee::getContactNo, EmployeeResponseDTO::setContactNo);
            mapper.map(Employee::getAddress, EmployeeResponseDTO::setAddress);
            mapper.map(Employee::getSalary, EmployeeResponseDTO::setSalary);
            mapper.map(Employee::getBirthDate, EmployeeResponseDTO::setBirthDate);
            mapper.map(Employee::getBloodGroup, EmployeeResponseDTO::setBloodGroup);

            // Nested mappings (these will now be allowed since no prior implicit)
            mapper.map(Employee::getDepartment, EmployeeResponseDTO::setDepartment);
            mapper.map(Employee::getDesignation, EmployeeResponseDTO::setDesignation);
            mapper.map(Employee::getHeadOfDepartment, EmployeeResponseDTO::setHeadOfDepartment);

            // If you want to SKIP the entire 'department' (or any nested object) in DTO
            // Do it HERE, before any nested implicit would apply
            // mapper.skip(Employee::getDepartment, EmployeeResponseDTO::setDepartment);
        });

        // If you still want implicit mapping for any unmatched fields (optional, usually not needed with STRICT)
        // employeeToDtoMap.implicitMappings();

        // Similarly fix Department → DepartmentResponseDTO and Designation → DesignationResponseDTO
        // Use emptyTypeMap() + addMappings() pattern if you have skips or custom logic there too

        return modelMapper;
    }
}