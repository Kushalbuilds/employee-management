package com.company.ems.config;

import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the database with a small set of demo departments and employees
 * the first time the app runs against an empty database. Safe to leave
 * enabled — it no-ops if data already exists.
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            return;
        }

        Department engineering = departmentRepository.save(
                Department.builder().name("Engineering").description("Builds and maintains our products").build());
        Department hr = departmentRepository.save(
                Department.builder().name("Human Resources").description("People operations and hiring").build());
        Department sales = departmentRepository.save(
                Department.builder().name("Sales").description("Revenue and client relationships").build());

        employeeRepository.save(Employee.builder()
                .firstName("Asha").lastName("Rao").email("asha.rao@company.com")
                .phone("+91-9876543210").jobTitle("Senior Software Engineer")
                .salary(new BigDecimal("1450000")).hireDate(LocalDate.of(2021, 6, 14))
                .status(Employee.EmployeeStatus.ACTIVE)
                .performanceLabel(Employee.PerformanceLabel.HIGH_PERFORMER)
                .department(engineering).build());

        employeeRepository.save(Employee.builder()
                .firstName("Rahul").lastName("Mehta").email("rahul.mehta@company.com")
                .phone("+91-9812345670").jobTitle("Software Engineer")
                .salary(new BigDecimal("950000")).hireDate(LocalDate.of(2023, 1, 9))
                .status(Employee.EmployeeStatus.ACTIVE)
                .performanceLabel(Employee.PerformanceLabel.TRAINEE)
                .department(engineering).build());

        employeeRepository.save(Employee.builder()
                .firstName("Priya").lastName("Nair").email("priya.nair@company.com")
                .phone("+91-9900112233").jobTitle("HR Manager")
                .salary(new BigDecimal("1100000")).hireDate(LocalDate.of(2020, 3, 2))
                .status(Employee.EmployeeStatus.ACTIVE).department(hr).build());

        employeeRepository.save(Employee.builder()
                .firstName("Vikram").lastName("Singh").email("vikram.singh@company.com")
                .phone("+91-9765432109").jobTitle("Sales Executive")
                .salary(new BigDecimal("700000")).hireDate(LocalDate.of(2022, 11, 20))
                .status(Employee.EmployeeStatus.ON_LEAVE)
                .performanceLabel(Employee.PerformanceLabel.UNDER_PERFORMER)
                .department(sales).build());

        employeeRepository.save(Employee.builder()
                .firstName("Ananya").lastName("Iyer").email("ananya.iyer@company.com")
                .phone("+91-9654321098").jobTitle("Sales Manager")
                .salary(new BigDecimal("1250000")).hireDate(LocalDate.of(2019, 8, 5))
                .status(Employee.EmployeeStatus.ACTIVE).department(sales).build());
    }
}
