let courses = [];
let departments = [];
let teachers = [];
let editingCourseId = null;

// Get authorization headers from session storage
function getAuthHeaders() {
    const authCredentials = sessionStorage.getItem('authCredentials');
    if (!authCredentials) {
        window.location.href = '/index.html';
        return {};
    }
    return {
        'Authorization': 'Basic ' + authCredentials,
        'Content-Type': 'application/json'
    };
}

// Load data when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in
    if (!sessionStorage.getItem('authCredentials')) {
        window.location.href = '/index.html';
        return;
    }
    loadDepartments();
    loadTeachers();
    loadCourses();
});

// Load departments for dropdown
async function loadDepartments() {
    try {
        const response = await fetch('/api/dept', {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            departments = await response.json();
            populateDepartmentDropdown();
        }
    } catch (error) {
        console.error('Error loading departments:', error);
    }
}

function populateDepartmentDropdown() {
    const select = document.getElementById('deptId');
    select.innerHTML = '<option value="">Select Department</option>';
    departments.forEach(dept => {
        const option = document.createElement('option');
        option.value = dept.id;
        option.textContent = dept.name;
        select.appendChild(option);
    });
}

// Load teachers for dropdown
async function loadTeachers() {
    try {
        const response = await fetch('/api/teacher', {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            teachers = await response.json();
            populateTeacherDropdown();
        }
    } catch (error) {
        console.error('Error loading teachers:', error);
    }
}

function populateTeacherDropdown() {
    const select = document.getElementById('teacherId');
    select.innerHTML = '<option value="">Select Teacher</option>';
    teachers.forEach(teacher => {
        const option = document.createElement('option');
        option.value = teacher.id;
        option.textContent = teacher.name;
        select.appendChild(option);
    });
}

// Load all courses
async function loadCourses() {
    try {
        const response = await fetch('/api/course', {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            courses = await response.json();
            displayCourses(courses);
        } else {
            showAlert('Failed to load courses', 'error');
        }
    } catch (error) {
        console.error('Error loading courses:', error);
        showAlert('Error loading courses', 'error');
    }
}

// Display courses in table
function displayCourses(courseList) {
    const container = document.getElementById('tableContainer');
    
    if (courseList.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                </svg>
                <h3>No Courses Found</h3>
                <p>Start by adding a new course using the button above.</p>
            </div>
        `;
        return;
    }

    let tableHTML = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Credits</th>
                    <th>Department</th>
                    <th>Teacher</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
    `;

    courseList.forEach(course => {
        tableHTML += `
            <tr>
                <td><span class="badge badge-blue">${course.id}</span></td>
                <td><strong>${course.code || '-'}</strong></td>
                <td>${course.name}</td>
                <td><span class="badge badge-purple">${course.credits || '-'}</span></td>
                <td>${course.dept ? `<span class="badge badge-green">${course.dept.name}</span>` : '-'}</td>
                <td>${course.teacher ? course.teacher.name : '-'}</td>
                <td>
                    <div class="actions">
                        <button class="btn btn-primary" onclick="editCourse(${course.id})">Edit</button>
                        <button class="btn btn-danger" onclick="deleteCourse(${course.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `;
    });

    tableHTML += `
            </tbody>
        </table>
    `;

    container.innerHTML = tableHTML;
}

// Search functionality
function searchCourses() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    if (!searchTerm) {
        displayCourses(courses);
        return;
    }

    const filtered = courses.filter(course => {
        return (
            course.name.toLowerCase().includes(searchTerm) ||
            (course.code && course.code.toLowerCase().includes(searchTerm)) ||
            (course.dept && course.dept.name.toLowerCase().includes(searchTerm)) ||
            (course.teacher && course.teacher.name.toLowerCase().includes(searchTerm))
        );
    });

    displayCourses(filtered);
}

// Open add modal
function openAddModal() {
    editingCourseId = null;
    document.getElementById('modalTitle').textContent = 'Add New Course';
    document.getElementById('courseForm').reset();
    document.getElementById('courseId').value = '';
    document.getElementById('courseModal').style.display = 'block';
}

// Edit course
function editCourse(id) {
    editingCourseId = id;
    const course = courses.find(c => c.id === id);
    
    if (course) {
        document.getElementById('modalTitle').textContent = 'Edit Course';
        document.getElementById('courseId').value = course.id;
        document.getElementById('code').value = course.code || '';
        document.getElementById('name').value = course.name;
        document.getElementById('credits').value = course.credits || '';
        document.getElementById('deptId').value = course.dept ? course.dept.id : '';
        document.getElementById('teacherId').value = course.teacher ? course.teacher.id : '';
        document.getElementById('courseModal').style.display = 'block';
    }
}

// Save course (add or update)
async function saveCourse() {
    const code = document.getElementById('code').value;
    const name = document.getElementById('name').value;
    const credits = document.getElementById('credits').value;
    const deptId = document.getElementById('deptId').value;
    const teacherId = document.getElementById('teacherId').value;

    if (!name) {
        showAlert('Please fill in all required fields', 'error');
        return;
    }

    const courseData = {
        code: code || null,
        name: name,
        credits: credits ? parseInt(credits) : null,
        dept: deptId ? { id: parseInt(deptId) } : null,
        teacher: teacherId ? { id: parseInt(teacherId) } : null
    };

    try {
        let response;
        if (editingCourseId) {
            // Update existing course
            response = await fetch(`/api/course/${editingCourseId}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(courseData)
            });
        } else {
            // Create new course
            response = await fetch('/api/course', {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(courseData)
            });
        }

        if (response.ok) {
            showAlert(editingCourseId ? 'Course updated successfully!' : 'Course added successfully!', 'success');
            closeModal();
            loadCourses();
        } else {
            showAlert('Failed to save course', 'error');
        }
    } catch (error) {
        console.error('Error saving course:', error);
        showAlert('Error saving course', 'error');
    }
}

// Delete course
async function deleteCourse(id) {
    if (!confirm('Are you sure you want to delete this course?')) {
        return;
    }

    try {
        const response = await fetch(`/api/course/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            showAlert('Course deleted successfully!', 'success');
            loadCourses();
        } else {
            showAlert('Failed to delete course', 'error');
        }
    } catch (error) {
        console.error('Error deleting course:', error);
        showAlert('Error deleting course', 'error');
    }
}

// Close modal
function closeModal() {
    document.getElementById('courseModal').style.display = 'none';
    document.getElementById('courseForm').reset();
    editingCourseId = null;
}

// Show alert message
function showAlert(message, type) {
    const alertBox = document.getElementById('alertBox');
    alertBox.textContent = message;
    alertBox.className = `alert alert-${type}`;
    alertBox.style.display = 'block';

    setTimeout(() => {
        alertBox.style.display = 'none';
    }, 3000);
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('courseModal');
    if (event.target === modal) {
        closeModal();
    }
}

// Enable search on Enter key
document.getElementById('searchInput').addEventListener('keyup', function(event) {
    if (event.key === 'Enter') {
        searchCourses();
    }
});

// Logout function
function logout() {
    sessionStorage.clear();
    window.location.href = '/index.html';
}
