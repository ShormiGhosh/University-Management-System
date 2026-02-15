let departments = [];
let editingDeptId = null;

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
});

// Load all departments
async function loadDepartments() {
    try {
        const response = await fetch('/api/dept', {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            departments = await response.json();
            displayDepartments(departments);
        } else {
            showAlert('Failed to load departments', 'error');
        }
    } catch (error) {
        console.error('Error loading departments:', error);
        showAlert('Error loading departments', 'error');
    }
}

// Display departments in table
function displayDepartments(deptList) {
    const container = document.getElementById('tableContainer');
    
    if (deptList.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                </svg>
                <h3>No Departments Found</h3>
                <p>Start by adding a new department using the button above.</p>
            </div>
        `;
        return;
    }

    let tableHTML = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
    `;

    deptList.forEach(dept => {
        tableHTML += `
            <tr>
                <td><span class="badge badge-blue">${dept.id}</span></td>
                <td><strong>${dept.name}</strong></td>
                <td>${dept.description || '-'}</td>
                <td>
                    <div class="actions">
                        <button class="btn btn-primary" onclick="editDepartment(${dept.id})">Edit</button>
                        <button class="btn btn-danger" onclick="deleteDepartment(${dept.id})">Delete</button>
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
function searchDepartments() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    if (!searchTerm) {
        displayDepartments(departments);
        return;
    }

    const filtered = departments.filter(dept => {
        return (
            dept.name.toLowerCase().includes(searchTerm) ||
            (dept.description && dept.description.toLowerCase().includes(searchTerm))
        );
    });

    displayDepartments(filtered);
}

// Open add modal
function openAddModal() {
    editingDeptId = null;
    document.getElementById('modalTitle').textContent = 'Add New Department';
    document.getElementById('deptForm').reset();
    document.getElementById('deptId').value = '';
    document.getElementById('deptModal').style.display = 'block';
}

// Edit department
function editDepartment(id) {
    editingDeptId = id;
    const dept = departments.find(d => d.id === id);
    
    if (dept) {
        document.getElementById('modalTitle').textContent = 'Edit Department';
        document.getElementById('deptId').value = dept.id;
        document.getElementById('name').value = dept.name;
        document.getElementById('description').value = dept.description || '';
        document.getElementById('deptModal').style.display = 'block';
    }
}

// Save department (add or update)
async function saveDepartment() {
    const name = document.getElementById('name').value;
    const description = document.getElementById('description').value;

    if (!name) {
        showAlert('Please enter department name', 'error');
        return;
    }

    const deptData = {
        name: name,
        description: description || null
    };

    try {
        let response;
        if (editingDeptId) {
            // Update existing department
            response = await fetch(`/api/dept/${editingDeptId}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(deptData)
            });
        } else {
            // Create new department
            response = await fetch('/api/dept', {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(deptData)
            });
        }

        if (response.ok) {
            showAlert(editingDeptId ? 'Department updated successfully!' : 'Department added successfully!', 'success');
            closeModal();
            loadDepartments();
        } else {
            showAlert('Failed to save department', 'error');
        }
    } catch (error) {
        console.error('Error saving department:', error);
        showAlert('Error saving department', 'error');
    }
}

// Delete department
async function deleteDepartment(id) {
    if (!confirm('Are you sure you want to delete this department?')) {
        return;
    }

    try {
        const response = await fetch(`/api/dept/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            showAlert('Department deleted successfully!', 'success');
            loadDepartments();
        } else {
            showAlert('Failed to delete department', 'error');
        }
    } catch (error) {
        console.error('Error deleting department:', error);
        showAlert('Error deleting department', 'error');
    }
}

// Close modal
function closeModal() {
    document.getElementById('deptModal').style.display = 'none';
    document.getElementById('deptForm').reset();
    editingDeptId = null;
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
    const modal = document.getElementById('deptModal');
    if (event.target === modal) {
        closeModal();
    }
}

// Enable search on Enter key
document.getElementById('searchInput').addEventListener('keyup', function(event) {
    if (event.key === 'Enter') {
        searchDepartments();
    }
});

// Logout function
function logout() {
    sessionStorage.clear();
    window.location.href = '/index.html';
}
