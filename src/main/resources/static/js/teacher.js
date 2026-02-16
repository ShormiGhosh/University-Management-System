let teachers = [];
let departments = [];
let editingTeacherId = null;

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

// Load all teachers
async function loadTeachers() {
    try {
        const response = await fetch('/api/teacher', {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            teachers = await response.json();
            displayTeachers(teachers);
        } else {
            showAlert('Failed to load teachers', 'error');
        }
    } catch (error) {
        console.error('Error loading teachers:', error);
        showAlert('Error loading teachers', 'error');
    }
}

// Display teachers in table
function displayTeachers(teacherList) {
    const container = document.getElementById('tableContainer');
    
    if (teacherList.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
                </svg>
                <h3>No Teachers Found</h3>
                <p>Start by adding a new teacher using the button above.</p>
            </div>
        `;
        return;
    }

    let tableHTML = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Employee ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Department</th>
                    <th>Specialization</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
    `;

    teacherList.forEach(teacher => {
        tableHTML += `
            <tr>
                <td><span class="badge badge-blue">${teacher.id}</span></td>
                <td><strong>${teacher.employeeId}</strong></td>
                <td>${teacher.name}</td>
                <td>${teacher.email || '-'}</td>
                <td>${teacher.phone || '-'}</td>
                <td>${teacher.dept ? `<span class="badge badge-green">${teacher.dept.name}</span>` : '-'}</td>
                <td>${teacher.specialization ? `<span class="badge badge-purple">${teacher.specialization}</span>` : '-'}</td>
                <td>
                    <div class="actions">
                        <button class="btn btn-primary" onclick="editTeacher(${teacher.id})">Edit</button>
                        <button class="btn btn-danger" onclick="deleteTeacher(${teacher.id})">Delete</button>
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
function searchTeachers() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    if (!searchTerm) {
        displayTeachers(teachers);
        return;
    }

    const filtered = teachers.filter(teacher => {
        return (
            teacher.name.toLowerCase().includes(searchTerm) ||
            teacher.employeeId.toLowerCase().includes(searchTerm) ||
            (teacher.email && teacher.email.toLowerCase().includes(searchTerm)) ||
            (teacher.phone && teacher.phone.toLowerCase().includes(searchTerm)) ||
            (teacher.specialization && teacher.specialization.toLowerCase().includes(searchTerm)) ||
            (teacher.dept && teacher.dept.name.toLowerCase().includes(searchTerm))
        );
    });

    displayTeachers(filtered);
}

// Open add modal
function openAddModal() {
    editingTeacherId = null;
    document.getElementById('modalTitle').textContent = 'Add New Teacher';
    document.getElementById('teacherForm').reset();
    document.getElementById('teacherId').value = '';
    document.getElementById('teacherModal').style.display = 'block';
}

// Edit teacher
function editTeacher(id) {
    editingTeacherId = id;
    const teacher = teachers.find(t => t.id === id);
    
    if (teacher) {
        document.getElementById('modalTitle').textContent = 'Edit Teacher';
        document.getElementById('teacherId').value = teacher.id;
        document.getElementById('employeeId').value = teacher.employeeId;
        document.getElementById('name').value = teacher.name;
        document.getElementById('email').value = teacher.email || '';
        document.getElementById('phone').value = teacher.phone || '';
        document.getElementById('deptId').value = teacher.dept ? teacher.dept.id : '';
        document.getElementById('specialization').value = teacher.specialization || '';
        document.getElementById('teacherModal').style.display = 'block';
    }
}

// Save teacher (add or update)
async function saveTeacher() {
    const employeeId = document.getElementById('employeeId').value;
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const deptId = document.getElementById('deptId').value;
    const specialization = document.getElementById('specialization').value;

    if (!employeeId || !name) {
        showAlert('Please fill in all required fields', 'error');
        return;
    }

    const teacherData = {
        employeeId: employeeId,
        name: name,
        email: email || null,
        phone: phone || null,
        specialization: specialization || null,
        dept: deptId ? { id: parseInt(deptId) } : null
    };

    try {
        let response;
        if (editingTeacherId) {
            // Update existing teacher
            response = await fetch(`/api/teacher/${editingTeacherId}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(teacherData)
            });
        } else {
            // Create new teacher
            response = await fetch('/api/teacher', {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(teacherData)
            });
        }

        if (response.ok) {
            showAlert(editingTeacherId ? 'Teacher updated successfully!' : 'Teacher added successfully!', 'success');
            closeModal();
            loadTeachers();
        } else {
            showAlert('Failed to save teacher', 'error');
        }
    } catch (error) {
        console.error('Error saving teacher:', error);
        showAlert('Error saving teacher', 'error');
    }
}

// Delete teacher
async function deleteTeacher(id) {
    if (!confirm('Are you sure you want to delete this teacher?')) {
        return;
    }

    try {
        const response = await fetch(`/api/teacher/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            showAlert('Teacher deleted successfully!', 'success');
            loadTeachers();
        } else {
            showAlert('Failed to delete teacher', 'error');
        }
    } catch (error) {
        console.error('Error deleting teacher:', error);
        showAlert('Error deleting teacher', 'error');
    }
}

// Close modal
function closeModal() {
    document.getElementById('teacherModal').style.display = 'none';
    document.getElementById('teacherForm').reset();
    editingTeacherId = null;
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
    const modal = document.getElementById('teacherModal');
    if (event.target === modal) {
        closeModal();
    }
}

// Enable search on Enter key
document.getElementById('searchInput').addEventListener('keyup', function(event) {
    if (event.key === 'Enter') {
        searchTeachers();
    }
});
