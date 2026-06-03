package com.employee.dto;

import com.employee.entity.Department;
import com.employee.enums.BloodGroup;
import com.employee.json_view.JsonViews;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponseDTO {
    @JsonView(JsonViews.Basic.class)
    private Long id;
    @JsonView(JsonViews.Basic.class)
    private String empName;
    @JsonView(JsonViews.Basic.class)
    private String empEmail;
    @JsonView(JsonViews.Detailed.class)
    private String contactNo;
    @JsonView(JsonViews.Detailed.class)
    private DepartmentResponseDTO department;
    @JsonView(JsonViews.Detailed.class)
    private DesignationResponseDTO designation;
    @JsonView(JsonViews.Full.class)
    private DepartmentResponseDTO headOfDepartment;  // Fixed: was Department (entity)
    @JsonView(JsonViews.Basic.class)
    private String address;
    @JsonView(JsonViews.Basic.class)
    private BigDecimal salary;
    @JsonView(JsonViews.Basic.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate birthDate;
    @JsonView(JsonViews.Basic.class)
    private BloodGroup bloodGroup;

}
