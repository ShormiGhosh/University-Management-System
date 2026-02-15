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

// Get role-based URL prefix
function getRolePrefix() {
    const role = sessionStorage.getItem('role');
    if (role === 'TEACHER') {
        return '/teacher';
    } else if (role === 'STUDENT') {
        return '/student';
    }
    return '';
}

// Check if user is logged in
function checkAuth() {
    if (!sessionStorage.getItem('authCredentials')) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

// Logout function
function logout() {
    sessionStorage.clear();
    window.location.href = '/index.html';
}

// Update navigation links based on role
function updateNavigationLinks() {
    const rolePrefix = getRolePrefix();
    const navButtons = document.querySelectorAll('.nav-btn');
    
    navButtons.forEach(btn => {
        const href = btn.getAttribute('href');
        if (href && !href.startsWith('http') && !href.startsWith('/teacher') && !href.startsWith('/student')) {
            btn.setAttribute('href', rolePrefix + '/' + href);
        }
    });
    
    const viewAllBtn = document.querySelector('.view-all-btn');
    if (viewAllBtn) {
        const href = viewAllBtn.getAttribute('href');
        if (href && !href.startsWith('http') && !href.startsWith('/teacher') && !href.startsWith('/student')) {
            viewAllBtn.setAttribute('href', rolePrefix + '/' + href);
        }
    }
}

// Load dashboard data
document.addEventListener('DOMContentLoaded', async function() {
    if (!checkAuth()) {
        return;
    }
    
    // Update navigation links with role prefix
    updateNavigationLinks();

    try {
        // Load statistics
        await loadStatistics();
        // Load recent students
        await loadRecentStudents();
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
});

// Load statistics
async function loadStatistics() {
    try {
        const headers = getAuthHeaders();
        
        // Fetch students count
        const studentsResponse = await fetch('/api/student', { headers });
        if (studentsResponse.ok) {
            const students = await studentsResponse.json();
            document.getElementById('studentCount').textContent = students.length;
        }

        // Fetch teachers count
        const teachersResponse = await fetch('/api/teacher', { headers });
        if (teachersResponse.ok) {
            const teachers = await teachersResponse.json();
            document.getElementById('teacherCount').textContent = teachers.length;
        }

        // Fetch courses count
        const coursesResponse = await fetch('/api/course', { headers });
        if (coursesResponse.ok) {
            const courses = await coursesResponse.json();
            document.getElementById('courseCount').textContent = courses.length;
        }

        // Fetch departments count
        const deptsResponse = await fetch('/api/dept', { headers });
        if (deptsResponse.ok) {
            const depts = await deptsResponse.json();
            document.getElementById('deptCount').textContent = depts.length;
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Load recent students
async function loadRecentStudents() {
    try {
        const headers = getAuthHeaders();
        const response = await fetch('/api/student', { headers });
        
        if (response.ok) {
            const students = await response.json();
            const recentStudents = students.slice(0, 5); // Get first 5 students
            
            const tbody = document.getElementById('recentStudents');
            
            if (recentStudents.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No students found</td></tr>';
                return;
            }
            
            tbody.innerHTML = recentStudents.map(student => `
                <tr>
                    <td><strong>${student.rollNumber}</strong></td>
                    <td>${student.name}</td>
                    <td>${student.email || '-'}</td>
                    <td>${student.dept ? student.dept.name : '-'}</td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading recent students:', error);
        const tbody = document.getElementById('recentStudents');
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">Error loading students</td></tr>';
    }
}
