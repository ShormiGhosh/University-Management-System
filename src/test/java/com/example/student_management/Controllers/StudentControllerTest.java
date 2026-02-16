package com.example.student_management.Controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.student_management.entity.Student;
import com.example.student_management.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
        objectMapper = new ObjectMapper();
        
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("John Doe");
        testStudent.setEmail("john@example.com");
        testStudent.setPhone("1234567890");
        testStudent.setRollNumber("S001");
    }

    @Test
    void getAllStudents() throws Exception {
        // Test retrieving all students
        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Jane Doe");
        student2.setEmail("jane@example.com");
        List<Student> students = Arrays.asList(testStudent, student2);
        
        when(studentService.findAll()).thenReturn(students);

        mockMvc.perform(get("/api/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(studentService, times(1)).findAll();
    }

    @Test
    void getStudentById() throws Exception {
        // Test retrieving student by ID when exists
        when(studentService.findById(1L)).thenReturn(Optional.of(testStudent));

        mockMvc.perform(get("/api/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.rollNumber").value("S001"));

        verify(studentService, times(1)).findById(1L);
        
        // Test retrieving student by ID when not exists
        when(studentService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/student/999"))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).findById(999L);
    }

}