package com.example.student_management.Controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.student_management.entity.Course;
import com.example.student_management.service.CourseService;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private MockMvc mockMvc;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
        
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Data Structures");
        testCourse.setCode("CS101");
        testCourse.setCredits(4);
    }

    @Test
    void getCourseById() throws Exception {
        // Test retrieving course by ID when exists
        when(courseService.findById(1L)).thenReturn(Optional.of(testCourse));

        mockMvc.perform(get("/api/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Data Structures"))
                .andExpect(jsonPath("$.code").value("CS101"))
                .andExpect(jsonPath("$.credits").value(4));

        verify(courseService, times(1)).findById(1L);
        
        // Test retrieving course by ID when not exists
        when(courseService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/course/999"))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).findById(999L);
    }

}