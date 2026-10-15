package com.company.ems;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.dto.EmployeeResponse;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.model.Employee;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void cleanUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    private EmployeeRequest sampleRequest(String email) {
        EmployeeRequest req = new EmployeeRequest();
        req.setFirstName("Test");
        req.setLastName("User");
        req.setEmail(email);
        req.setJobTitle("QA Engineer");
        req.setSalary(new BigDecimal("500000"));
        req.setHireDate(LocalDate.of(2024, 1, 1));
        req.setStatus(Employee.EmployeeStatus.ACTIVE);
        return req;
    }

    @Test
    void createsEmployeeSuccessfully() {
        EmployeeResponse created = employeeService.create(sampleRequest("test.user@company.com"));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getFullName()).isEqualTo("Test User");
        assertThat(created.getEmail()).isEqualTo("test.user@company.com");
    }

    @Test
    void rejectsDuplicateEmail() {
        employeeService.create(sampleRequest("dup@company.com"));

        assertThatThrownBy(() -> employeeService.create(sampleRequest("dup@company.com")))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void throwsWhenEmployeeNotFound() {
        assertThatThrownBy(() -> employeeService.getById(9999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatesEmployee() {
        EmployeeResponse created = employeeService.create(sampleRequest("update@company.com"));

        EmployeeRequest updateReq = sampleRequest("update@company.com");
        updateReq.setJobTitle("Senior QA Engineer");
        updateReq.setSalary(new BigDecimal("650000"));

        EmployeeResponse updated = employeeService.update(created.getId(), updateReq);

        assertThat(updated.getJobTitle()).isEqualTo("Senior QA Engineer");
        assertThat(updated.getSalary()).isEqualByComparingTo("650000");
    }

    @Test
    void deletesEmployee() {
        EmployeeResponse created = employeeService.create(sampleRequest("delete@company.com"));

        employeeService.delete(created.getId());

        assertThatThrownBy(() -> employeeService.getById(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchFiltersByKeyword() {
        employeeService.create(sampleRequest("alice@company.com"));
        EmployeeRequest bobReq = sampleRequest("bob@company.com");
        bobReq.setFirstName("Bob");
        employeeService.create(bobReq);

        var page = employeeService.search("bob", null, null, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getFirstName()).isEqualTo("Bob");
    }
}
