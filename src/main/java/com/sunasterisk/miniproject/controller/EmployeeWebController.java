package com.sunasterisk.miniproject.controller;

import com.sunasterisk.miniproject.dto.EmployeeRequest;
import com.sunasterisk.miniproject.dto.EmployeeResponse;
import com.sunasterisk.miniproject.dto.RegisterRequest;
import com.sunasterisk.miniproject.entity.User.Role;
import com.sunasterisk.miniproject.exception.DuplicateResourceException;
import com.sunasterisk.miniproject.service.AuthService;
import com.sunasterisk.miniproject.service.DepartmentService;
import com.sunasterisk.miniproject.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeWebController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final AuthService authService;

    public EmployeeWebController(
            EmployeeService employeeService,
            DepartmentService departmentService,
            AuthService authService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            request.setRole(Role.USER);
            authService.register(request);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please sign in.");
            return "redirect:/login";
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/employees")
    public String listEmployees(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<EmployeeResponse> employeePage = employeeService.findAll(
                keyword, PageRequest.of(page, size, Sort.by("id").ascending()));
        model.addAttribute("employeePage", employeePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalEmployees", employeeService.getTotalEmployeeCount());
        return "employees/list";
    }

    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new EmployeeRequest());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("isEdit", false);
        return "employees/form";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(
            @Valid @ModelAttribute("employee") EmployeeRequest employeeRequest,
            BindingResult bindingResult,
            @RequestParam(required = false) Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("isEdit", id != null);
            model.addAttribute("employeeId", id);
            return "employees/form";
        }
        try {
            if (id == null) {
                employeeService.create(employeeRequest);
                redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully.");
            } else {
                employeeService.update(id, employeeRequest);
                redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully.");
            }
            return "redirect:/employees";
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("isEdit", id != null);
            model.addAttribute("employeeId", id);
            return "employees/form";
        }
    }

    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        EmployeeResponse existing = employeeService.findById(id);
        EmployeeRequest form = new EmployeeRequest();
        form.setName(existing.getName());
        form.setEmail(existing.getEmail());
        form.setPhone(existing.getPhone());
        form.setPosition(existing.getPosition());
        form.setSalary(existing.getSalary());
        form.setDepartmentId(existing.getDepartmentId());

        model.addAttribute("employee", form);
        model.addAttribute("employeeId", id);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("isEdit", true);
        return "employees/form";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        employeeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully.");
        return "redirect:/employees";
    }

    @GetMapping("/employees/statistics")
    public String showStatistics(Model model) {
        model.addAttribute("statistics", employeeService.getStatistics());
        return "employees/statistics";
    }
}
