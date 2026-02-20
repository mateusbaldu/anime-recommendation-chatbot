import axios from 'axios';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor: automatically attach auth headers to every request
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    const guestSessionId = localStorage.getItem('guestSessionId');

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    } else if (guestSessionId) {
        config.headers['X-Guest-Session-Id'] = guestSessionId;
    }

    return config;
});
