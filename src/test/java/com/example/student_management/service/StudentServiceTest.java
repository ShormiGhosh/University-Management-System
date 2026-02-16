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

import com.example.student_management.entity.Student;
import com.example.student_management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("John Doe");
        testStudent.setEmail("john@example.com");
        testStudent.setPhone("1234567890");
        testStudent.setRollNumber("S001");
    }

    @Test
    void findAll() {
        // Test retrieving all students
        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Jane Doe");
        List<Student> students = Arrays.asList(testStudent, student2);
        
        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = studentService.findAll();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void findByRollNumber() {
        // Test finding student by roll number
        when(studentRepository.findByRollNumber("S001")).thenReturn(Optional.of(testStudent));

        Optional<Student> result = studentService.findByRollNumber("S001");

        assertTrue(result.isPresent());
        assertEquals("S001", result.get().getRollNumber());
        assertEquals("John Doe", result.get().getName());
        verify(studentRepository, times(1)).findByRollNumber("S001");
    }

    @Test
    void save() {
        // Test saving a new student
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        Student result = studentService.save(testStudent);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(studentRepository, times(1)).save(testStudent);
    }
}