let students = [];
let departments = [];
let editingStudentId = null;

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
    loadStudents();
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

// Load all students
async function loadStudents() {
    try {
        const response = await fetch('/api/student', {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            students = await response.json();
            displayStudents(students);
        } else {
            showAlert('Failed to load students', 'error');
        }
    } catch (error) {
        console.error('Error loading students:', error);
        showAlert('Error loading students', 'error');
    }
}

// Display students in table
function displayStudents(studentList) {
    const container = document.getElementById('tableContainer');
    
    if (studentList.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
                <h3>No Students Found</h3>
                <p>Start by adding a new student using the button above.</p>
            </div>
        `;
        return;
    }

    let tableHTML = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Roll Number</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Department</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
    `;

    studentList.forEach(student => {
        tableHTML += `
            <tr>
                <td><span class="badge badge-blue">${student.id}</span></td>
                <td><strong>${student.rollNumber}</strong></td>
                <td>${student.name}</td>
                <td>${student.email || '-'}</td>
                <td>${student.phone || '-'}</td>
                <td>${student.dept ? `<span class="badge badge-green">${student.dept.name}</span>` : '-'}</td>
                <td>
                    <div class="actions">
                        <button class="btn btn-primary" onclick="editStudent(${student.id})">Edit</button>
                        <button class="btn btn-danger" onclick="deleteStudent(${student.id})">Delete</button>
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
function searchStudents() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    if (!searchTerm) {
        displayStudents(students);
        return;
    }

    const filtered = students.filter(student => {
        return (
            student.name.toLowerCase().includes(searchTerm) ||
            student.rollNumber.toLowerCase().includes(searchTerm) ||
            (student.email && student.email.toLowerCase().includes(searchTerm)) ||
            (student.phone && student.phone.toLowerCase().includes(searchTerm)) ||
            (student.dept && student.dept.name.toLowerCase().includes(searchTerm))
        );
    });

    displayStudents(filtered);
}

// Open add modal
function openAddModal() {
    editingStudentId = null;
    document.getElementById('modalTitle').textContent = 'Add New Student';
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    document.getElementById('studentModal').style.display = 'block';
}

// Edit student
function editStudent(id) {
    editingStudentId = id;
    const student = students.find(s => s.id === id);
    
    if (student) {
        document.getElementById('modalTitle').textContent = 'Edit Student';
        document.getElementById('studentId').value = student.id;
        document.getElementById('rollNumber').value = student.rollNumber;
        document.getElementById('name').value = student.name;
        document.getElementById('email').value = student.email || '';
        document.getElementById('phone').value = student.phone || '';
        document.getElementById('deptId').value = student.dept ? student.dept.id : '';
        document.getElementById('studentModal').style.display = 'block';
    }
}

// Save student (add or update)
async function saveStudent() {
    const rollNumber = document.getElementById('rollNumber').value;
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const deptId = document.getElementById('deptId').value;

    if (!rollNumber || !name) {
        showAlert('Please fill in all required fields', 'error');
        return;
    }

    const studentData = {
        rollNumber: rollNumber,
        name: name,
        email: email || null,
        phone: phone || null,
        dept: deptId ? { id: parseInt(deptId) } : null
    };

    try {
        let response;
        if (editingStudentId) {
            // Update existing student
            response = await fetch(`/api/student/${editingStudentId}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(studentData)
            });
        } else {
            // Create new student
            response = await fetch('/api/student', {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(studentData)
            });
        }

        if (response.ok) {
            showAlert(editingStudentId ? 'Student updated successfully!' : 'Student added successfully!', 'success');
            closeModal();
            loadStudents();
        } else {
            showAlert('Failed to save student', 'error');
        }
    } catch (error) {
        console.error('Error saving student:', error);
        showAlert('Error saving student', 'error');
    }
}

// Delete student
async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) {
        return;
    }

    try {
        const response = await fetch(`/api/student/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            showAlert('Student deleted successfully!', 'success');
            loadStudents();
        } else {
            showAlert('Failed to delete student', 'error');
        }
    } catch (error) {
        console.error('Error deleting student:', error);
        showAlert('Error deleting student', 'error');
    }
}

// Close modal
function closeModal() {
    document.getElementById('studentModal').style.display = 'none';
    document.getElementById('studentForm').reset();
    editingStudentId = null;
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
    const modal = document.getElementById('studentModal');
    if (event.target === modal) {
        closeModal();
    }
}

// Enable search on Enter key
document.getElementById('searchInput').addEventListener('keyup', function(event) {
    if (event.key === 'Enter') {
        searchStudents();
    }
});
