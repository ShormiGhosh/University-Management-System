package com.example.student_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.student_management.entity.Dept;
import com.example.student_management.repository.DeptRepository;

@ExtendWith(MockitoExtension.class)
class DeptServiceTest {

    @Mock
    private DeptRepository deptRepository;

    @InjectMocks
    private DeptService deptService;

    private Dept testDept;

    @BeforeEach
    void setUp() {
        testDept = new Dept();
        testDept.setId(1L);
        testDept.setName("Computer Science");
        testDept.setDescription("Department of Computer Science and Engineering");
    }

    @Test
    void update() {
        // Test updating existing department
        Dept updatedDetails = new Dept();
        updatedDetails.setName("Computer Science and Engineering");
        updatedDetails.setDescription("Updated department description");

        when(deptRepository.findById(1L)).thenReturn(Optional.of(testDept));
        when(deptRepository.save(any(Dept.class))).thenReturn(testDept);

        Dept result = deptService.update(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Computer Science and Engineering", result.getName());
        assertEquals("Updated department description", result.getDescription());
        verify(deptRepository, times(1)).findById(1L);
        verify(deptRepository, times(1)).save(any(Dept.class));
    }
}