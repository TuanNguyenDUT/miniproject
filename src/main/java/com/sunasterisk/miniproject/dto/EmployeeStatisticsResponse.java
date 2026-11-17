package com.sunasterisk.miniproject.dto;

import java.util.List;

public class EmployeeStatisticsResponse {

    private long totalEmployees;
    private List<DepartmentStatisticsDto> departmentStatistics;

    public EmployeeStatisticsResponse(long totalEmployees, List<DepartmentStatisticsDto> departmentStatistics) {
        this.totalEmployees = totalEmployees;
        this.departmentStatistics = departmentStatistics;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public List<DepartmentStatisticsDto> getDepartmentStatistics() {
        return departmentStatistics;
    }

    public void setDepartmentStatistics(List<DepartmentStatisticsDto> departmentStatistics) {
        this.departmentStatistics = departmentStatistics;
    }
}
