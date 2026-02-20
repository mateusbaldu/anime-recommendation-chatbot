import { api } from './api';
import type { ChatMessage } from '../pages/ChatPage';

let currentSessionId: string | null = null;

async function getOrCreateSession(): Promise<string> {
    if (currentSessionId) return currentSessionId;


    try {
        const response = await api.get('/chats', { params: { size: 1, sort: 'updatedAt,desc' } });
        const sessions = response.data?.content;
        if (sessions && sessions.length > 0) {
            currentSessionId = sessions[0].id;
            return currentSessionId!;
        }
    } catch {
    }

    const response = await api.post('/chats');
    currentSessionId = response.data.id;
    return currentSessionId!;
}

export const chatService = {
    sendMessage: async (content: string): Promise<string> => {
        const sessionId = await getOrCreateSession();


        const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
        const guestSessionId = localStorage.getItem('guestSessionId');

        const response = await fetch(`${baseURL}/chats/${sessionId}/messages`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(guestSessionId ? { 'X-Guest-Session-Id': guestSessionId } : {}),
            },
            body: JSON.stringify({ message: content }),
        });

        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }


        const reader = response.body?.getReader();
        if (!reader) throw new Error('No response body');

        const decoder = new TextDecoder();
        let fullResponse = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            const chunk = decoder.decode(value, { stream: true });

            const lines = chunk.split('\n');
            for (const line of lines) {
                if (line.startsWith('data:')) {
                    fullResponse += line.substring(5).trim();
                }
            }
        }

        return fullResponse || 'No response received from the AI.';
    },


    getHistory: async (): Promise<ChatMessage[]> => {

        const response = await api.get('/chats', { params: { size: 1, sort: 'updatedAt,desc' } });
        const sessions = response.data?.content;

        if (!sessions || sessions.length === 0) {
            return [];
        }

        const latestSession = sessions[0];


        currentSessionId = latestSession.id;


        let rawMessages: Array<{ role: string; content: string }> = [];
        try {
            rawMessages = typeof latestSession.messages === 'string'
                ? JSON.parse(latestSession.messages)
                : latestSession.messages;
        } catch {
            console.error('Failed to parse session messages JSON');
            return [];
        }


        return rawMessages
            .filter((msg) => msg.role !== 'system')
            .map((msg) => ({
                id: crypto.randomUUID(),
                role: msg.role === 'user' ? 'user' : 'bot',
                content: msg.content,
            })) as ChatMessage[];
    },

    resetSession: () => {
        currentSessionId = null;
    }
};

