import { ModeToggle } from '../mode-toggle';

export function Header() {
    return (
        <header className="flex h-14 items-center border-b px-4 lg:h-16">
            <h1 className="text-xl font-bold">AnimeRec Chatbot</h1>
            <div className="ml-auto">
                <ModeToggle />
            </div>
        </header>
    );
}
