package com.employee.repository;

import com.employee.entity.Department;
import com.employee.projection.DepartmentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    // Derived Queries
    boolean existsByDepartmentNameIgnoreCase(String departmentName);
    Optional<Department> findByIdAndEmployeesIsDeletedFalse(Long id); // For soft-delete awareness

    // JPQL - Fetch with Employees and Designations
    @Query("SELECT DISTINCT d FROM Department d " +
            "LEFT JOIN FETCH d.employees e " +
            "LEFT JOIN FETCH d.designations " +
            "WHERE d.id = :id")
    Optional<Department> findByIdWithAssociations(@Param("id") Long id);

    // Native SQL - Count employees per department
    @Query(value = "SELECT d.id, d.department_name, COUNT(e.id) as employee_count " +
            "FROM department d " +
            "LEFT JOIN employee e ON d.id = e.department_id AND e.is_deleted = false " +
            "GROUP BY d.id, d.department_name", nativeQuery = true)
    List<Object[]> getDepartmentEmployeeCountsNative();

    // Projection Query
    @Query("SELECT d.id AS id, d.departmentName AS departmentName, " +
            "COUNT(e) AS employeeCount " +
            "FROM Department d LEFT JOIN d.employees e " +
            "WHERE e.isDeleted = false OR e IS NULL " +
            "GROUP BY d.id, d.departmentName")
    List<DepartmentProjection> getDepartmentEmployeeCounts();

    // Search by location (JPQL)
    @Query("SELECT d FROM Department d WHERE d.location LIKE %:location%")
    List<Department> findByLocationContaining(@Param("location") String location);
}
