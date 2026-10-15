package com.company.ems.service;

import com.company.ems.dto.DepartmentDto;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.model.Department;
import com.company.ems.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public DepartmentDto getById(Long id) {
        Department department = findOrThrow(id);
        return toDto(department);
    }

    @Transactional
    public DepartmentDto create(DepartmentDto dto) {
        if (departmentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("A department named '" + dto.getName() + "' already exists");
        }
        Department department = new Department(null, dto.getName().trim(), dto.getDescription(), new java.util.ArrayList<>());
        return toDto(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentDto update(Long id, DepartmentDto dto) {
        Department department = findOrThrow(id);

        departmentRepository.findByNameIgnoreCase(dto.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("A department named '" + dto.getName() + "' already exists");
                });

        department.setName(dto.getName().trim());
        department.setDescription(dto.getDescription());
        return toDto(departmentRepository.save(department));
    }

    @Transactional
    public void delete(Long id) {
        Department department = findOrThrow(id);
        if (!department.getEmployees().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete department '" + department.getName() + "' because it has assigned employees");
        }
        departmentRepository.delete(department);
    }

    private Department findOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Department", id));
    }

    private DepartmentDto toDto(Department department) {
        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .employeeCount(department.getEmployees() == null ? 0 : department.getEmployees().size())
                .build();
    }
}
