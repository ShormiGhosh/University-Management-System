package com.example.student_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.student_management.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}