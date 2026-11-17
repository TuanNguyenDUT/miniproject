package com.sunasterisk.miniproject.repository;

import com.sunasterisk.miniproject.dto.DepartmentStatisticsDto;
import com.sunasterisk.miniproject.entity.Employee;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Page<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Employee e")
    long countTotalEmployees();

    @Query("SELECT new com.sunasterisk.miniproject.dto.DepartmentStatisticsDto(d.name, COUNT(e)) "
            + "FROM Department d LEFT JOIN d.employees e GROUP BY d.id, d.name ORDER BY COUNT(e) DESC")
    List<DepartmentStatisticsDto> countEmployeesByDepartment();
}
