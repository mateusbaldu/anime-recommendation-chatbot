import { Sidebar } from '../components/layout/Sidebar';
import { Header } from '../components/layout/Header';
import { ChatInterface } from '../features/chat/ChatInterface';
export function Home() {

    return (
        <div className="flex h-screen bg-background text-foreground">
            <Sidebar />
            <div className="flex-1 flex flex-col h-full overflow-hidden">
                <Header />
                <main className="flex-1 p-6 overflow-hidden flex flex-col items-center">

                    <div className="w-full max-w-4xl h-full pb-8">
                        <ChatInterface />
                    </div>
                </main>
            </div>
        </div>
    );
}
