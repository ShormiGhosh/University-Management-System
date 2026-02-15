package com.example.student_management.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.student_management.entity.Course;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    private Course testCourse1;
    private Course testCourse2;

    @BeforeEach
    void setUp() {
        testCourse1 = new Course();
        testCourse1.setName("Data Structures");
        testCourse1.setCode("CS101");
        testCourse1.setCredits(4);

        testCourse2 = new Course();
        testCourse2.setName("Algorithms");
        testCourse2.setCode("CS201");
        testCourse2.setCredits(3);
    }
}