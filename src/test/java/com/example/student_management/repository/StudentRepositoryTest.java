package com.example.student_management.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.student_management.entity.Student;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    private Student testStudent1;
    private Student testStudent2;

    @BeforeEach
    void setUp() {
        testStudent1 = new Student();
        testStudent1.setName("John Doe");
        testStudent1.setEmail("john@example.com");
        testStudent1.setPhone("1234567890");
        testStudent1.setRollNumber("S001");

        testStudent2 = new Student();
        testStudent2.setName("Jane Smith");
        testStudent2.setEmail("jane@example.com");
        testStudent2.setPhone("0987654321");
        testStudent2.setRollNumber("S002");
    }

    @Test
    void findByRollNumber() {
        // Test finding by roll number when student exists
        entityManager.persist(testStudent1);
        entityManager.flush();

        Optional<Student> found = studentRepository.findByRollNumber("S001");

        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
        assertEquals("S001", found.get().getRollNumber());
        assertEquals("john@example.com", found.get().getEmail());
        assertEquals("1234567890", found.get().getPhone());
        
        // Test finding by roll number when student does not exist
        Optional<Student> notFound = studentRepository.findByRollNumber("S999");
        assertFalse(notFound.isPresent());
    }
}