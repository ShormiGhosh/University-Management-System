package com.example.student_management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.student_management.entity.Course;
import com.example.student_management.entity.Dept;
import com.example.student_management.entity.Student;
import com.example.student_management.entity.Teacher;
import com.example.student_management.entity.User;
import com.example.student_management.repository.CourseRepository;
import com.example.student_management.repository.DeptRepository;
import com.example.student_management.repository.StudentRepository;
import com.example.student_management.repository.TeacherRepository;
import com.example.student_management.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DeptRepository deptRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, DeptRepository deptRepository,
                           TeacherRepository teacherRepository, StudentRepository studentRepository,
                           CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.deptRepository = deptRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only initialize if no users exist
        if (userRepository.count() > 0) {
            return;
        }

        // Create Department
        Dept csDept = new Dept();
        csDept.setName("Computer Science");
        csDept.setDescription("Department of Computer Science");
        deptRepository.save(csDept);

        // Create Teacher
        Teacher teacher = new Teacher();
        teacher.setEmployeeId("EMP001");
        teacher.setName("Dr. John Smith");
        teacher.setEmail("john.smith@university.com");
        teacher.setPhone("1234567890");
        teacher.setSpecialization("Software Engineering");
        teacher.setDept(csDept);
        teacherRepository.save(teacher);

        // Create Student
        Student student = new Student();
        student.setRollNumber("CS2024001");
        student.setName("Jane Doe");
        student.setEmail("jane.doe@university.com");
        student.setPhone("0987654321");
        student.setDept(csDept);
        studentRepository.save(student);

        // Create Course
        Course course = new Course();
        course.setName("Introduction to Programming");
        course.setCode("CS101");
        course.setCredits(3);
        course.setDept(csDept);
        course.setTeacher(teacher);
        courseRepository.save(course);

        // Create Teacher User
        User teacherUser = new User();
        teacherUser.setUsername("teacher");
        teacherUser.setPassword(passwordEncoder.encode("teacher123"));
        teacherUser.setRole(User.Role.TEACHER);
        teacherUser.setTeacher(teacher);
        userRepository.save(teacherUser);

        // Create Student User
        User studentUser = new User();
        studentUser.setUsername("student");
        studentUser.setPassword(passwordEncoder.encode("student123"));
        studentUser.setRole(User.Role.STUDENT);
        studentUser.setStudent(student);
        userRepository.save(studentUser);

        System.out.println("=================================");
        System.out.println("Sample data initialized!");
        System.out.println("Teacher login: teacher / teacher123");
        System.out.println("Student login: student / student123");
        System.out.println("=================================");
    }
}