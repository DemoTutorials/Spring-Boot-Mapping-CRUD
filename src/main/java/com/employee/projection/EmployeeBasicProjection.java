package com.employee.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBasicProjection {
    private Long id;
    private String empName;
    private String empEmail;

}
