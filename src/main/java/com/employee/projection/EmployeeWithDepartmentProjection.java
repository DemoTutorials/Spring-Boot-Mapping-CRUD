package com.employee.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWithDepartmentProjection {
    private String empName;
    private String departmentName;
    private String designationName;
}
