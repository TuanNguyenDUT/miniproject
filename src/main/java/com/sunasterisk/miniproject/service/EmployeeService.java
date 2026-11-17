package com.sunasterisk.miniproject.service;

import com.sunasterisk.miniproject.config.CacheConfig;
import com.sunasterisk.miniproject.dto.DepartmentStatisticsDto;
import com.sunasterisk.miniproject.dto.EmployeeRequest;
import com.sunasterisk.miniproject.dto.EmployeeResponse;
import com.sunasterisk.miniproject.dto.EmployeeStatisticsResponse;
import com.sunasterisk.miniproject.entity.Department;
import com.sunasterisk.miniproject.entity.Employee;
import com.sunasterisk.miniproject.exception.DuplicateResourceException;
import com.sunasterisk.miniproject.exception.ResourceNotFoundException;
import com.sunasterisk.miniproject.repository.DepartmentRepository;
import com.sunasterisk.miniproject.repository.EmployeeRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public Page<EmployeeResponse> findAll(String keyword, Pageable pageable) {
        Page<Employee> page;
        if (StringUtils.hasText(keyword)) {
            page = employeeRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    keyword, keyword, pageable);
        } else {
            page = employeeRepository.findAll(pageable);
        }
        return page.map(EmployeeResponse::fromEntity);
    }

    public EmployeeResponse findById(Long id) {
        Employee employee = getEmployeeEntity(id);
        return EmployeeResponse.fromEntity(employee);
    }

    public Employee getEmployeeEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Transactional
    @CacheEvict(value = CacheConfig.EMPLOYEE_COUNT_CACHE, allEntries = true)
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }
        Employee employee = new Employee();
        applyRequest(employee, request);
        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Transactional
    @CacheEvict(value = CacheConfig.EMPLOYEE_COUNT_CACHE, allEntries = true)
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = getEmployeeEntity(id);
        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }
        applyRequest(employee, request);
        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Transactional
    @CacheEvict(value = CacheConfig.EMPLOYEE_COUNT_CACHE, allEntries = true)
    public void delete(Long id) {
        Employee employee = getEmployeeEntity(id);
        employeeRepository.delete(employee);
    }

    @Cacheable(value = CacheConfig.EMPLOYEE_COUNT_CACHE)
    public long getTotalEmployeeCount() {
        return employeeRepository.countTotalEmployees();
    }

    public EmployeeStatisticsResponse getStatistics() {
        long totalEmployees = employeeRepository.countTotalEmployees();
        List<DepartmentStatisticsDto> byDepartment = employeeRepository.countEmployeesByDepartment();
        return new EmployeeStatisticsResponse(totalEmployees, byDepartment);
    }

    private void applyRequest(Employee employee, EmployeeRequest request) {
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found with id: " + request.getDepartmentId()));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }
    }
}
