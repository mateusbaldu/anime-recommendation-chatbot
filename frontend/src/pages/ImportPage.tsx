import { ImportCSV } from '../features/import/ImportCSV';
import { Sidebar } from '../components/layout/Sidebar';
import { Header } from '../components/layout/Header';

export function ImportPage() {
    return (
        <div className="flex h-screen bg-background text-foreground">
            <Sidebar />
            <div className="flex-1 flex flex-col h-full overflow-hidden">
                <Header />
                <main className="flex-1 p-6 flex flex-col items-center justify-center">
                    <ImportCSV />
                </main>
            </div>
        </div>
    );
}
