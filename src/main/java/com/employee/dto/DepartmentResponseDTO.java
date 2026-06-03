package com.employee.dto;

import com.employee.json_view.JsonViews;
import com.employee.projection.DepartmentProjection;
import com.employee.projection.DesignationProjection;
import com.employee.projection.EmployeeBasicProjection;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponseDTO {
    @JsonView(JsonViews.Basic.class)
    private Long id;

    @JsonView(JsonViews.Basic.class)
    private String departmentName;

    @JsonView(JsonViews.Detailed.class)
    private String location;

    @JsonView(JsonViews.Detailed.class)
    private String description;

    @JsonView(JsonViews.Full.class)
    private List<EmployeeBasicProjection> employees;

    @JsonView(JsonViews.Full.class)
    private List<DesignationProjection> designations;  // Fixed: was List<DepartmentProjection> departments

    @JsonView(JsonViews.Detailed.class)
    private EmployeeBasicProjection head;
}
