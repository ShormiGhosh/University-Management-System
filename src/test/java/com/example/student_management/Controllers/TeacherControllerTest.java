package com.example.student_management.Controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.student_management.entity.Teacher;
import com.example.student_management.service.TeacherService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private TeacherController teacherController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
        objectMapper = new ObjectMapper();
        
        testTeacher = new Teacher();
        testTeacher.setId(1L);
        testTeacher.setName("Dr. Smith");
        testTeacher.setEmail("smith@example.com");
        testTeacher.setPhone("1234567890");
        testTeacher.setEmployeeId("T001");
        testTeacher.setSpecialization("Computer Science");
    }

    @Test
    void createTeacher() throws Exception {
        // Test creating a new teacher
        when(teacherService.save(any(Teacher.class))).thenReturn(testTeacher);
        String teacherJson = objectMapper.writeValueAsString(testTeacher);

        mockMvc.perform(post("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(teacherJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dr. Smith"))
                .andExpect(jsonPath("$.email").value("smith@example.com"))
                .andExpect(jsonPath("$.employeeId").value("T001"))
                .andExpect(jsonPath("$.specialization").value("Computer Science"));

        verify(teacherService, times(1)).save(any(Teacher.class));
    }

    @Test
    void updateTeacher() throws Exception {
        // Test updating existing teacher
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(1L);
        updatedTeacher.setName("Dr. Smith Updated");
        updatedTeacher.setEmail("smith.updated@example.com");
        updatedTeacher.setPhone("9876543210");
        updatedTeacher.setEmployeeId("T001");
        updatedTeacher.setSpecialization("Data Science");

        when(teacherService.update(eq(1L), any(Teacher.class))).thenReturn(updatedTeacher);
        String teacherJson = objectMapper.writeValueAsString(updatedTeacher);

        mockMvc.perform(put("/api/teacher/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(teacherJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Smith Updated"))
                .andExpect(jsonPath("$.email").value("smith.updated@example.com"))
                .andExpect(jsonPath("$.specialization").value("Data Science"));

        verify(teacherService, times(1)).update(eq(1L), any(Teacher.class));
        
        // Test updating non-existent teacher
        when(teacherService.update(eq(999L), any(Teacher.class)))
                .thenThrow(new RuntimeException("Teacher not found"));

        mockMvc.perform(put("/api/teacher/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(teacherJson))
                .andExpect(status().isNotFound());

        verify(teacherService, times(1)).update(eq(999L), any(Teacher.class));
    }

    @Test
    void deleteTeacher() throws Exception {
        // Test deleting a teacher
        doNothing().when(teacherService).deleteById(1L);

        mockMvc.perform(delete("/api/teacher/1"))
                .andExpect(status().isNoContent());

        verify(teacherService, times(1)).deleteById(1L);
    }
}