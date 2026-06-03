package com.employee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentRequestDTO {
    @NotBlank
    private String departmentName;
    private String location;
    private String description;
    private List<Long> designationIds;   // for ManyToMany
    private Long headEmployeeId;         // for OneToOne
}
