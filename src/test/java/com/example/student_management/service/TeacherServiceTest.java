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

import com.example.student_management.entity.Teacher;
import com.example.student_management.repository.TeacherRepository;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        testTeacher = new Teacher();
        testTeacher.setId(1L);
        testTeacher.setName("Dr. Smith");
        testTeacher.setEmail("smith@example.com");
        testTeacher.setPhone("1234567890");
        testTeacher.setEmployeeId("T001");
        testTeacher.setSpecialization("Computer Science");
    }

    @Test
    void findById() {
        // Test finding teacher by ID
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));

        Optional<Teacher> result = teacherService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Dr. Smith", result.get().getName());
        assertEquals("smith@example.com", result.get().getEmail());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void update() {
        // Test updating existing teacher
        Teacher updatedDetails = new Teacher();
        updatedDetails.setName("Dr. Smith Updated");
        updatedDetails.setEmail("smith.updated@example.com");
        updatedDetails.setPhone("9876543210");
        updatedDetails.setSpecialization("Data Science");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(testTeacher);

        Teacher result = teacherService.update(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Dr. Smith Updated", result.getName());
        assertEquals("smith.updated@example.com", result.getEmail());
        assertEquals("Data Science", result.getSpecialization());
        verify(teacherRepository, times(1)).findById(1L);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }
}