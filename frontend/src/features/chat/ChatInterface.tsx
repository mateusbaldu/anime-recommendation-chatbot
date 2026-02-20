import { useState } from 'react';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useChat } from '../../hooks/useChat';

export function ChatInterface() {
    const { messages, sendMessage, isLoading } = useChat();
    const [input, setInput] = useState('');

    const handleSend = () => {
        if (!input.trim()) return;
        sendMessage(input);
        setInput('');
    };

    return (
        <div className="flex flex-col h-[80vh] w-full max-w-3xl mx-auto border rounded-lg bg-background overflow-hidden relative shadow-sm">
            <div className="flex-1 overflow-y-auto p-4 space-y-4">
                {messages.map((msg) => (
                    <div key={msg.id} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                        <div className={`p-4 rounded-xl shadow-sm max-w-[80%] ${msg.role === 'user' ? 'bg-primary text-primary-foreground rounded-br-none' : 'bg-muted text-foreground rounded-bl-none'}`}>
                            {msg.text}
                        </div>
                    </div>
                ))}
                {isLoading && (
                    <div className="flex justify-start">
                        <div className="p-3 rounded-lg max-w-[80%] bg-muted text-muted-foreground italic text-sm">
                            AnimeRec Bot is typing...
                        </div>
                    </div>
                )}
            </div>
            <div className="p-4 border-t flex gap-2 bg-card">
                <Input
                    disabled={isLoading}
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    placeholder="Ask for an anime recommendation..."
                    onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                    className="flex-1"
                />
                <Button disabled={isLoading} onClick={handleSend} className="px-6">Send</Button>
            </div>
        </div>
    );
}
