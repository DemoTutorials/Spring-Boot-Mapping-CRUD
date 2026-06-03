package com.employee.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentProjection {
    private Long id;
    private String departmentName;
    private Long employeeCount;
}
