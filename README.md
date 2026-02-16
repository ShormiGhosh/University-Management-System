# University Management System

A simple university management system built with Java Spring Boot following MVC architecture and REST API principles.

## Features

- **Authentication & Authorization**: Role-based access control using Spring Security
- **Two Roles**: Student and Teacher
- **Entity Relationships**:
  - Student → Department (Many-to-One)
  - Teacher → Department (Many-to-One)
  - Student ↔ Course (Many-to-Many)
  - Course → Teacher (Many-to-One)
  - Course → Department (Many-to-One)
- **Access Control**:
  - Teachers can add/delete courses
  - Students cannot manage courses
  - Students can update their information except Roll Number
  - Teachers have full CRUD access to student information
- **Database**: PostgreSQL with Docker support

## Technologies

- Java 17
- Spring Boot 4.0.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose

## Project Structure

```
student_management/
├── src/main/java/com/example/student_management/
│   ├── Controllers/          # REST API Controllers
│   ├── entity/              # JPA Entities
│   ├── repository/          # Data Access Layer
│   ├── service/             # Business Logic Layer
│   └── config/              # Security & Data Initialization
├── src/main/resources/
│   ├── static/              # Frontend HTML/CSS/JS files
│   └── application.properties
├── Dockerfile
└── compose.yaml
```

## Setup & Running

### Using Docker (Recommended)

1. Start PostgreSQL and application:
```bash
docker-compose up -d
```

2. Application will be available at: `http://localhost:8080`

### Manual Setup

1. Start PostgreSQL:
```bash
docker run -d -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123 -e POSTGRES_DB=university_db postgres:latest
```

2. Run the application:
```bash
./mvnw spring-boot:run
```

## Default Credentials

| Role     | Username | Password    |
|----------|----------|-------------|
| Teacher  | teacher  | teacher123  |
| Student  | student  | student123  |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `GET /api/auth/login` - Login (use Basic Auth)
- `GET /api/auth/me` - Get current user info

### Students (Both roles can read, Teacher can modify)
- `GET /api/student` - Get all students
- `GET /api/student/{id}` - Get student by ID
- `POST /api/student` - Create student (Teacher only)
- `PUT /api/student/{id}` - Update student
- `DELETE /api/student/{id}` - Delete student (Teacher only)

### Teachers (Teacher role required for modifications)
- `GET /api/teacher` - Get all teachers
- `GET /api/teacher/{id}` - Get teacher by ID
- `POST /api/teacher` - Create teacher
- `PUT /api/teacher/{id}` - Update teacher
- `DELETE /api/teacher/{id}` - Delete teacher

### Courses (Teacher role required for modifications)
- `GET /api/course` - Get all courses
- `GET /api/course/{id}` - Get course by ID
- `POST /api/course` - Create course (Teacher only)
- `PUT /api/course/{id}` - Update course (Teacher only)
- `DELETE /api/course/{id}` - Delete course (Teacher only)

### Departments (Teacher role required for modifications)
- `GET /api/dept` - Get all departments
- `GET /api/dept/{id}` - Get department by ID
- `POST /api/dept` - Create department (Teacher only)
- `PUT /api/dept/{id}` - Update department (Teacher only)
- `DELETE /api/dept/{id}` - Delete department (Teacher only)

## Database Schema

### Entity Relationships

```
Department (1) ←→ (Many) Student
Department (1) ←→ (Many) Teacher
Department (1) ←→ (Many) Course
Teacher (1) ←→ (Many) Course
Student (Many) ←→ (Many) Course
User (1) ←→ (1) Student/Teacher
```

## Example Requests

### Login as Teacher
```bash
curl -u teacher:teacher123 http://localhost:8080/api/teacher
```

### Create a New Student (Teacher only)
```bash
curl -u teacher:teacher123 -X POST http://localhost:8080/api/student \
  -H "Content-Type: application/json" \
  -d '{
    "rollNumber": "CS2024002",
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "dept": {"id": 1}
  }'
```

### Update Student Information (Student can update except rollNumber)
```bash
curl -u student:student123 -X PUT http://localhost:8080/api/student/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "phone": "9876543210"
  }'
```

### Create a Course (Teacher only)
```bash
curl -u teacher:teacher123 -X POST http://localhost:8080/api/course \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Data Structures",
    "code": "CS102",
    "credits": 4,
    "dept": {"id": 1},
    "teacher": {"id": 1}
  }'
```

## Frontend

Access the web interface at:
- Login: `http://localhost:8080/`
- Dashboard: `http://localhost:8080/dashboard.html`
- Students: `http://localhost:8080/student.html`
- Teachers: `http://localhost:8080/teacher.html`
- Courses: `http://localhost:8080/courses.html`
- Departments: `http://localhost:8080/dept.html`

## Notes

- The application uses Basic Authentication
- All passwords are encrypted using BCrypt
- Sample data is automatically created on first run
- Database schema is auto-created/updated by Hibernate
- No complex error handling - keeps code simple and clean
