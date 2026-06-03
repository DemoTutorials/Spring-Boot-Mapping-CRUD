package com.employee.repository;

import com.employee.entity.Employee;
import com.employee.projection.EmployeeBasicProjection;
import com.employee.projection.EmployeeWithDepartmentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    boolean existsByEmpEmailOrContactNoAndIsDeletedFalse(String empEmail, String contactNo);

    List<Employee> findAllByIsDeletedFalse();

    Optional<Employee> findByIdAndIsDeletedFalse(Long id);

    // Core
    boolean existsByEmpEmailAndIsDeletedFalse(String empEmail);
    boolean existsByContactNoAndIsDeletedFalse(String contactNo);

    // Associations
    List<Employee> findByDepartmentIdAndIsDeletedFalse(Long departmentId);
    List<Employee> findByDesignationIdAndIsDeletedFalse(Long designationId);

    // JPQL Fetch Joins
    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.department " +
            "JOIN FETCH e.designation " +
            "WHERE e.id = :id AND e.isDeleted = false")
    Optional<Employee> findByIdWithDepartmentAndDesignation(@Param("id") Long id);

    // Projections
    @Query("SELECT e.id AS id, e.empName AS empName, e.empEmail AS empEmail " +
            "FROM Employee e WHERE e.isDeleted = false")
    List<EmployeeBasicProjection> findAllBasicProjections();

    @Query("SELECT e.empName AS empName, d.departmentName AS departmentName, " +
            "des.designationName AS designationName " +
            "FROM Employee e " +
            "JOIN e.department d JOIN e.designation des " +
            "WHERE e.isDeleted = false")
    List<EmployeeWithDepartmentProjection> findAllWithDepartmentAndDesignation();

    // Native SQL Example
    @Query(value = "SELECT * FROM employee e WHERE e.department_id = :deptId AND e.is_deleted = false", nativeQuery = true)
    List<Employee> findByDepartmentNative(@Param("deptId") Long deptId);
}
