package com.example.student_management.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.student_management.entity.Dept;
import com.example.student_management.service.DeptService;

@RestController
@RequestMapping("/api/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @GetMapping
    public ResponseEntity<List<Dept>> getAllDepartments() {
        return ResponseEntity.ok(deptService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dept> getDepartmentById(@PathVariable Long id) {
        return deptService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Dept> createDepartment(@RequestBody Dept dept) {
        Dept savedDept = deptService.save(dept);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDept);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Dept> updateDepartment(@PathVariable Long id, @RequestBody Dept dept) {
        try {
            Dept updatedDept = deptService.update(id, dept);
            return ResponseEntity.ok(updatedDept);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        deptService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
