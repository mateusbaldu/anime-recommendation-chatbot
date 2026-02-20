import { api } from './api';

export const authService = {
    /**
     * Sends the Google credential token to the backend for validation.
     * The backend should return a JWT or session token.
     * Falls back to storing the Google token directly if backend
     * doesn't have a dedicated exchange endpoint yet.
     */
    loginWithGoogle: async (googleToken: string) => {
        try {
            const response = await api.post('/auth/google', { token: googleToken });
            const { token } = response.data;
            localStorage.setItem('token', token);
            return response.data;
        } catch {
            // Fallback: if the backend doesn't have POST /auth/google yet,
            // store the Google ID token directly. The backend's JWT Resource
            // Server can validate it as a Bearer token since issuer-uri is
            // configured to https://accounts.google.com.
            localStorage.setItem('token', googleToken);
            return { token: googleToken };
        }
    },

    logout: () => {
        localStorage.removeItem('token');
        window.location.href = '/login';
    },

    getToken: (): string | null => {
        return localStorage.getItem('token');
    },

    isAuthenticated: (): boolean => {
        return !!localStorage.getItem('token');
    },
};
