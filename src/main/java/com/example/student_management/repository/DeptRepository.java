package com.example.student_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.student_management.entity.Dept;

@Repository
public interface DeptRepository extends JpaRepository<Dept, Long> {
}