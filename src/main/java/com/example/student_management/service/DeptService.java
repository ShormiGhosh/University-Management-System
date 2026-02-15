package com.example.student_management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.student_management.entity.Dept;
import com.example.student_management.repository.DeptRepository;

@Service
public class DeptService {

    private final DeptRepository deptRepository;

    public DeptService(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    public List<Dept> findAll() {
        return deptRepository.findAll();
    }

    public Optional<Dept> findById(Long id) {
        return deptRepository.findById(id);
    }

    public Dept save(Dept dept) {
        return deptRepository.save(dept);
    }

    public Dept update(Long id, Dept deptDetails) {
        return deptRepository.findById(id)
                .map(dept -> {
                    dept.setName(deptDetails.getName());
                    dept.setDescription(deptDetails.getDescription());
                    return deptRepository.save(dept);
                })
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    public void deleteById(Long id) {
        deptRepository.deleteById(id);
    }
}
