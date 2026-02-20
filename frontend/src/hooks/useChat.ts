import { useState } from 'react';

export type Message = {
    id: string;
    role: 'user' | 'bot';
    text: string;
};

// Hook for chat logic
export function useChat() {
    const [messages, setMessages] = useState<Message[]>([
        { id: 'initial-bot-1', role: 'bot', text: 'Hello! I am your anime recommendation bot. How can I help you today?' }
    ]);
    const [isLoading, setIsLoading] = useState(false);

    const sendMessage = async (text: string) => {
        const newUserMsg: Message = { id: Date.now().toString(), role: 'user', text };
        setMessages(prev => [...prev, newUserMsg]);
        setIsLoading(true);

        // Simulate API call
        setTimeout(() => {
            const botResponse: Message = { id: Date.now().toString() + '-bot', role: 'bot', text: 'This is a mocked response. Integration with /api/v1/chat/sessions pending.' };
            setMessages(prev => [...prev, botResponse]);
            setIsLoading(false);
        }, 1000);
    };

    return { messages, sendMessage, isLoading };
}
