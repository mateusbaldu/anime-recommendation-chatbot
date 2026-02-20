import { Link } from 'react-router-dom';

export function Sidebar() {
    return (
        <aside className="w-64 border-r h-full flex flex-col p-4 bg-muted/20">
            <nav className="space-y-2">
                <Link to="/" className="block p-2 rounded hover:bg-muted font-medium">Chat</Link>
                <Link to="/import" className="block p-2 rounded hover:bg-muted font-medium">Import Anime List</Link>
            </nav>
        </aside>
    );
}
