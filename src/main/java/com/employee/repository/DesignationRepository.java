package com.employee.repository;

import com.employee.entity.Designation;
import com.employee.projection.DesignationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation,Long> {
    List<Designation> findAllByIdIn(List<Long> designationIds);

    boolean existsByDesignationNameIgnoreCase(String designationName);

    // Projection
    @Query("SELECT d.id AS id, d.designationName AS designationName, COUNT(e) AS employeeCount " +
            "FROM Designation d LEFT JOIN d.employees e " +
            "GROUP BY d.id, d.designationName")
    List<DesignationProjection> getDesignationEmployeeCounts();

    // JPQL with Employees
    @Query("SELECT d FROM Designation d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Designation findByIdWithEmployees(Long id);

}
