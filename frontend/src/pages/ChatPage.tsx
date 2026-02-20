import { useState, useRef, useEffect } from 'react';
import { Send, Loader2, Bot, User, Sparkles } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { chatService } from '../services/chatService';
import { RecommendationCard } from '../components/chat/RecommendationCard';

export interface ChatMessage {
    id: string;
    role: 'user' | 'bot';
    content: string;
}

function parseMessageContent(text: string) {
    const regex = /\|\|\|CARD\|\|\|([\s\S]*?)\|\|\|END_CARD\|\|\|/;
    const match = text.match(regex);

    if (match) {
        const jsonString = match[1];
        try {
            const data = JSON.parse(jsonString);
            const normalText = text.replace(regex, '').trim();
            return { normalText, extractedData: data };
        } catch (error) {
            console.error("Failed to parse card data:", error);
        }
    }

    return { normalText: text, extractedData: null };
}

export function ChatPage() {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [input, setInput] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const messagesEndRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);


    useEffect(() => {
        const fetchHistory = async () => {
            try {
                const history = await chatService.getHistory();
                if (history && history.length > 0) {
                    setMessages(history);
                }
            } catch (error) {
                console.error('Failed to load history:', error);
            }
        };
        fetchHistory();
    }, []);


    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, isLoading]);

    const handleSend = async () => {
        const content = input.trim();
        if (!content || isLoading) return;


        const userMessage: ChatMessage = {
            id: crypto.randomUUID(),
            role: 'user',
            content,
        };
        setMessages((prev) => [...prev, userMessage]);
        setInput('');
        setIsLoading(true);

        try {
            const responseContent = await chatService.sendMessage(content);

            const botMessage: ChatMessage = {
                id: crypto.randomUUID(),
                role: 'bot',
                content: responseContent,
            };
            setMessages((prev) => [...prev, botMessage]);
        } catch (err) {
            console.error('Failed to send message:', err);
            const errorMessage: ChatMessage = {
                id: crypto.randomUUID(),
                role: 'bot',
                content: '⚠️ Something went wrong. Please try again.',
            };
            setMessages((prev) => [...prev, errorMessage]);
        } finally {
            setIsLoading(false);
            inputRef.current?.focus();
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
        <div className="flex flex-col h-[calc(100vh-3.5rem)] lg:h-[calc(100vh-4rem)] bg-background">

            <div className="flex-1 overflow-y-auto px-4 py-6">
                <div className="mx-auto max-w-3xl space-y-6">

                    {messages.length === 0 && !isLoading && (
                        <div className="flex flex-col items-center justify-center h-full min-h-[60vh] gap-4 text-center">
                            <div className="relative">
                                <div className="absolute -inset-4 rounded-full bg-primary/10 blur-xl animate-pulse" />
                                <div className="relative flex h-20 w-20 items-center justify-center rounded-2xl bg-gradient-to-br from-primary/20 to-primary/5 border border-primary/20 shadow-lg">
                                    <Sparkles className="h-10 w-10 text-primary" />
                                </div>
                            </div>
                            <div className="space-y-2 mt-2">
                                <h2 className="text-2xl font-bold tracking-tight">
                                    What would you like to watch?
                                </h2>
                                <p className="text-muted-foreground max-w-sm mx-auto">
                                    Tell me your mood, a genre, or an anime you loved — I'll find your next favorite.
                                </p>
                            </div>
                        </div>
                    )}


                    {messages.map((msg) => {
                        const { normalText, extractedData } = parseMessageContent(msg.content);
                        return (
                            <div
                                key={msg.id}
                                className={`flex items-end gap-3 ${msg.role === 'user' ? 'justify-end' : 'justify-start'
                                    }`}
                            >

                                {msg.role === 'bot' && (
                                    <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-primary/30 to-primary/10 border border-primary/20 shadow-sm">
                                        <Bot className="h-4 w-4 text-primary" />
                                    </div>
                                )}

                                <div className={`flex flex-col gap-2 max-w-[80%] ${msg.role === 'user' ? 'items-end' : 'items-start'}`}>

                                    {normalText && (
                                        <div
                                            className={`relative rounded-2xl px-5 py-3.5 text-sm md:text-base leading-relaxed shadow-sm transition-all ${msg.role === 'user'
                                                ? 'bg-primary text-primary-foreground rounded-br-sm'
                                                : 'bg-muted text-foreground rounded-bl-sm'
                                                }`}
                                        >
                                            <p className="whitespace-pre-wrap break-words">{normalText}</p>
                                        </div>
                                    )}


                                    {extractedData && (
                                        <div className="w-full">
                                            <RecommendationCard data={extractedData} />
                                        </div>
                                    )}
                                </div>


                                {msg.role === 'user' && (
                                    <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-secondary to-secondary/50 border border-border shadow-sm">
                                        <User className="h-4 w-4 text-secondary-foreground" />
                                    </div>
                                )}
                            </div>
                        )
                    })}


                    {isLoading && (
                        <div className="flex items-end gap-3 justify-start">
                            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-primary/30 to-primary/10 border border-primary/20 shadow-sm">
                                <Bot className="h-4 w-4 text-primary" />
                            </div>
                            <div className="rounded-2xl rounded-bl-sm bg-muted px-4 py-3 shadow-sm">
                                <div className="flex items-center gap-1.5 h-5">
                                    <span
                                        className="inline-block h-2 w-2 rounded-full bg-muted-foreground/60 animate-bounce"
                                        style={{ animationDelay: '0ms' }}
                                    />
                                    <span
                                        className="inline-block h-2 w-2 rounded-full bg-muted-foreground/60 animate-bounce"
                                        style={{ animationDelay: '150ms' }}
                                    />
                                    <span
                                        className="inline-block h-2 w-2 rounded-full bg-muted-foreground/60 animate-bounce"
                                        style={{ animationDelay: '300ms' }}
                                    />
                                </div>
                            </div>
                        </div>
                    )}


                    <div ref={messagesEndRef} className="h-2" />
                </div>
            </div>


            <div className="border-t bg-background/80 backdrop-blur-lg p-4">
                <div className="mx-auto flex max-w-3xl items-center gap-3">
                    <Input
                        ref={inputRef}
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="Ask for anime recommendations..."
                        disabled={isLoading}
                        className="flex-1 rounded-full bg-muted/50 border-border/50 focus-visible:ring-primary/30 h-12 px-6 text-base"
                        id="chat-input"
                    />
                    <Button
                        onClick={handleSend}
                        disabled={isLoading || !input.trim()}
                        className="h-12 w-12 rounded-full shrink-0 p-0 shadow-md hover:shadow-lg transition-shadow bg-primary text-primary-foreground"
                        id="chat-send-button"
                    >
                        {isLoading ? (
                            <Loader2 className="h-5 w-5 text-primary-foreground animate-spin" />
                        ) : (
                            <Send className="h-5 w-5 text-primary-foreground" />
                        )}
                    </Button>
                </div>
            </div>
        </div>
    );
}
