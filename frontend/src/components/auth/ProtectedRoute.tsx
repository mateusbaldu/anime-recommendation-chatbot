import { Navigate, Outlet } from 'react-router-dom';

export function ProtectedRoute() {
    const token = localStorage.getItem('token');
    const guestSessionId = localStorage.getItem('guestSessionId');

    if (!token && !guestSessionId) {
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}
