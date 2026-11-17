package com.sunasterisk.miniproject.config;

import com.sunasterisk.miniproject.entity.Department;
import com.sunasterisk.miniproject.entity.Employee;
import com.sunasterisk.miniproject.entity.User;
import com.sunasterisk.miniproject.entity.User.Role;
import com.sunasterisk.miniproject.repository.DepartmentRepository;
import com.sunasterisk.miniproject.repository.EmployeeRepository;
import com.sunasterisk.miniproject.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.saveAll(List.of(
                    new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN),
                    new User("user", passwordEncoder.encode("user123"), Role.USER)
            ));
        }

        if (departmentRepository.count() == 0) {
            Department engineering = departmentRepository.save(
                    new Department("Engineering", "Software development and infrastructure"));
            Department hr = departmentRepository.save(
                    new Department("Human Resources", "Talent acquisition and people operations"));
            Department marketing = departmentRepository.save(
                    new Department("Marketing", "Brand, growth, and communications"));
            Department finance = departmentRepository.save(
                    new Department("Finance", "Accounting and financial planning"));

            employeeRepository.saveAll(List.of(
                    new Employee("Nguyen Van An", "an.nguyen@sun-asterisk.vn", "0901234561",
                            "Backend Developer", new BigDecimal("22000000"), engineering),
                    new Employee("Tran Thi Binh", "binh.tran@sun-asterisk.vn", "0901234562",
                            "Frontend Developer", new BigDecimal("20000000"), engineering),
                    new Employee("Le Hoang Cuong", "cuong.le@sun-asterisk.vn", "0901234563",
                            "Tech Lead", new BigDecimal("35000000"), engineering),
                    new Employee("Pham Minh Dung", "dung.pham@sun-asterisk.vn", "0901234564",
                            "HR Specialist", new BigDecimal("16000000"), hr),
                    new Employee("Hoang Thu Ha", "ha.hoang@sun-asterisk.vn", "0901234565",
                            "Marketing Executive", new BigDecimal("18000000"), marketing),
                    new Employee("Vo Quoc Khanh", "khanh.vo@sun-asterisk.vn", "0901234566",
                            "Accountant", new BigDecimal("19000000"), finance)
            ));
        }
    }
}
