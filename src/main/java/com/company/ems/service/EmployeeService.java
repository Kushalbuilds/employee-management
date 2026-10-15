package com.company.ems.service;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.dto.EmployeeResponse;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.repository.EmployeeSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public Page<EmployeeResponse> search(String keyword, Long departmentId, Employee.EmployeeStatus status, Pageable pageable) {
        Specification<Employee> spec = Specification
                .where(EmployeeSpecifications.hasKeyword(keyword))
                .and(EmployeeSpecifications.hasDepartment(departmentId))
                .and(EmployeeSpecifications.hasStatus(status));

        return employeeRepository.findAll(spec, pageable).map(EmployeeResponse::fromEntity);
    }

    public EmployeeResponse getById(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("An employee with email '" + request.getEmail() + "' already exists");
        }

        Employee employee = new Employee();
        applyRequestToEntity(request, employee);

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeOrThrow(id);

        employeeRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("An employee with email '" + request.getEmail() + "' already exists");
                });

        applyRequestToEntity(request, employee);
        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw ResourceNotFoundException.forEntity("Employee", id);
        }
        employeeRepository.deleteById(id);
    }

    private void applyRequestToEntity(EmployeeRequest request, Employee employee) {
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setEmail(request.getEmail().trim().toLowerCase());
        employee.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        employee.setJobTitle(request.getJobTitle().trim());
        employee.setSalary(request.getSalary());
        employee.setHireDate(request.getHireDate());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : Employee.EmployeeStatus.ACTIVE);
        employee.setPerformanceLabel(request.getPerformanceLabel() != null ? request.getPerformanceLabel() : Employee.PerformanceLabel.UNASSIGNED);

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> ResourceNotFoundException.forEntity("Department", request.getDepartmentId()));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Employee", id));
    }
}
