package com.employee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesignationRequestDTO {
    @NotBlank(message = "Designation Name is Required")
    private String designationName;
    private String level;
    private List<Long> departmentIds;
    private List<Long> employeeIds;
}
