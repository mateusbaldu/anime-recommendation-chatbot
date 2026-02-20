import axios from 'axios';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});


api.interceptors.request.use((config) => {
    const guestSessionId = localStorage.getItem('guestSessionId');

    if (guestSessionId) {
        config.headers['X-Guest-Session-Id'] = guestSessionId;
    }

    return config;
});
