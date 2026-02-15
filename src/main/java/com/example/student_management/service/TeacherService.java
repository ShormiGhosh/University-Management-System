package com.example.student_management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.student_management.entity.Teacher;
import com.example.student_management.repository.TeacherRepository;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher update(Long id, Teacher teacherDetails) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacher.setName(teacherDetails.getName());
                    teacher.setEmail(teacherDetails.getEmail());
                    teacher.setPhone(teacherDetails.getPhone());
                    teacher.setSpecialization(teacherDetails.getSpecialization());
                    teacher.setDept(teacherDetails.getDept());
                    if (teacherDetails.getEmployeeId() != null) {
                        teacher.setEmployeeId(teacherDetails.getEmployeeId());
                    }
                    return teacherRepository.save(teacher);
                })
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
    }

    public void deleteById(Long id) {
        teacherRepository.deleteById(id);
    }
}
