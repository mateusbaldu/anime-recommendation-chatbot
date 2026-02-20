import { Outlet } from 'react-router-dom';
import { ModeToggle } from '../mode-toggle';

export function RootLayout() {
    return (
        <div className="min-h-screen bg-background text-foreground">
            <header className="flex h-14 items-center justify-between border-b px-4 lg:h-16">
                <h1 className="text-xl font-bold">AnimeRec Chatbot</h1>
                <ModeToggle />
            </header>
            <main>
                <Outlet />
            </main>
        </div>
    );
}
