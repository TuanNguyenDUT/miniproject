package com.sunasterisk.miniproject.service;

import com.sunasterisk.miniproject.entity.Department;
import com.sunasterisk.miniproject.exception.DuplicateResourceException;
import com.sunasterisk.miniproject.exception.ResourceNotFoundException;
import com.sunasterisk.miniproject.repository.DepartmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Transactional
    public Department create(Department department) {
        if (departmentRepository.existsByNameIgnoreCase(department.getName())) {
            throw new DuplicateResourceException("Department already exists: " + department.getName());
        }
        return departmentRepository.save(department);
    }
}
