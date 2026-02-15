// Role-based URL helper
function getRolePrefix() {
    const role = sessionStorage.getItem('role');
    if (role === 'TEACHER') {
        return '/teacher';
    } else if (role === 'STUDENT') {
        return '/student';
    }
    return '';
}

function getPageUrl(page) {
    return `${getRolePrefix()}/${page}`;
}

// Check authentication and redirect if needed
function checkAuthAndRedirect() {
    if (!sessionStorage.getItem('authCredentials')) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}
