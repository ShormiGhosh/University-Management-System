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

import com.example.student_management.entity.Course;
import com.example.student_management.repository.CourseRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Data Structures");
        testCourse.setCode("CS101");
        testCourse.setCredits(4);
    }

    @Test
    void save() {
        // Test saving a new course
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.save(testCourse);

        assertNotNull(result);
        assertEquals("Data Structures", result.getName());
        assertEquals("CS101", result.getCode());
        assertEquals(4, result.getCredits());
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    void deleteById() {
        // Test deleting a course
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteById(1L);

        verify(courseRepository, times(1)).deleteById(1L);
    }
}