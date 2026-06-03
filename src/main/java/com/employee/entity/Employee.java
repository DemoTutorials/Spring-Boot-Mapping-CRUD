package com.employee.entity;

import com.employee.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name",nullable = false)
    private String empName;

    @Column(name = "Email",nullable = false,unique = true)
    private String empEmail;

    @Column(name = "Contact",nullable = false,unique = true)
    private String contactNo;

    @Column(name = "Address",nullable = false,columnDefinition = "TEXT")
    private String address;

    @Column(name = "Salary",nullable = false)
    private BigDecimal salary;

    @Column(name = "Birth_Date",nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "BloodGroup",nullable = false)
    private BloodGroup bloodGroup;

    @Column(name = "Deleted")
    private Boolean isDeleted=false;

    @Column(name = "Active")
    private Boolean isActive=true;

    @CreatedDate
    @Column(name = "Created_DateTime")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "Updated_DateTime")
    private LocalDateTime updatedAt;

    // ManyToOne (Employee → Department) - Bi-directional with Department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // ManyToOne (Employee → Designation) - Bi-directional with Designation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    // OneToOne (Employee is Head of exactly one Department)
    @OneToOne(mappedBy = "head")
    private Department headOfDepartment;

    @PrePersist
    protected void create(){
    createdAt=LocalDateTime.now();
    }

    @PreUpdate
    protected void update(){
        updatedAt=LocalDateTime.now();
    }



}

