import { Outlet } from 'react-router-dom';
import { ModeToggle } from '../mode-toggle';
import { Button } from '../ui/Button';
import { LogOut } from 'lucide-react';

export function RootLayout() {
    const handleResetSession = () => {
        localStorage.clear();
        window.location.href = '/';
    };

    return (
        <div className="min-h-screen bg-background text-foreground">
            <header className="flex h-14 items-center justify-between border-b px-4 lg:h-16">
                <h1 className="text-xl font-bold">AnimeRec Chatbot</h1>
                <div className="flex items-center gap-2">
                    <Button variant="ghost" className="h-10 w-10 p-0" onClick={handleResetSession} title="Reset Session">
                        <LogOut className="h-5 w-5" />
                        <span className="sr-only">Reset Session</span>
                    </Button>
                    <ModeToggle />
                </div>
            </header>
            <main>
                <Outlet />
            </main>
        </div>
    );
}
