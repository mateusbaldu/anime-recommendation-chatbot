import { useNavigate } from 'react-router-dom';
import { ImportCSV } from '../features/import/ImportCSV';
import { Button } from '../components/ui/Button';

export function OnboardingPage() {
    const navigate = useNavigate();

    const handleSkip = () => {
        localStorage.setItem('hasCompletedOnboarding', 'true');
        navigate('/chat', { replace: true });
    };

    const handleImportCompleted = () => {
        localStorage.setItem('hasCompletedOnboarding', 'true');
        navigate('/chat', { replace: true });
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-[80vh] space-y-6">
            <h1 className="text-3xl font-bold">Bem-vindo(a) ao Chatbot</h1>
            <ImportCSV onImportCompleted={handleImportCompleted} />
            <div className="flex flex-col items-center space-y-2 mt-4">
                <p className="text-sm text-muted-foreground">Ou se preferir não enviar nada agora:</p>
                <Button variant="outline" onClick={handleSkip}>
                    Saltar para o Chat
                </Button>
            </div>
        </div>
    );
}
