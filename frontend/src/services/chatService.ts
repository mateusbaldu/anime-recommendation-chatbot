import { api } from './api';

export const chatService = {
    createSession: async () => {
        const response = await api.post('/api/v1/chat/sessions');
        return response.data;
    },

    sendMessage: async (sessionId: string, message: string) => {
        const response = await api.post(`/api/v1/chat/sessions/${sessionId}/messages`, { message });
        return response.data;
    },

    getSessions: async () => {
        const response = await api.get('/api/v1/chat/sessions');
        return response.data;
    }
};
