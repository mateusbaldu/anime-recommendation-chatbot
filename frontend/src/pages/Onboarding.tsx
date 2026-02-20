import { useNavigate } from 'react-router-dom';
import { Card } from '../components/ui/Card';

export function Onboarding() {
    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-muted/20 p-6">
            <div className="max-w-3xl w-full space-y-8 text-center">
                <div className="space-y-4">
                    <h1 className="text-4xl font-extrabold tracking-tight">Como quer começar?</h1>
                    <p className="text-xl text-muted-foreground">Escolha a melhor opção para personalizar sua experiência com o AnimeRec.</p>
                </div>

                <div className="grid md:grid-cols-2 gap-6 mt-8">
                    <Card
                        className="p-8 cursor-pointer hover:bg-muted/50 transition-colors border-2 hover:border-primary/50 flex flex-col items-center text-center space-y-4"
                        onClick={() => navigate('/import')}
                    >
                        <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center mb-2">
                            <svg xmlns="http://www.w3.org/-svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-primary"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" /><polyline points="17 8 12 3 7 8" /><line x1="12" x2="12" y1="3" y2="15" /></svg>
                        </div>
                        <h2 className="text-2xl font-bold">Importar perfil</h2>
                        <p className="text-muted-foreground">
                            Conecte sua conta do Letterboxd, IMDB ou MyAnimeList para receber recomendações ultra-precisas desde o primeiro momento.
                        </p>
                    </Card>

                    <Card
                        className="p-8 cursor-pointer hover:bg-muted/50 transition-colors border-2 hover:border-primary/50 flex flex-col items-center text-center space-y-4"
                        onClick={() => navigate('/')}
                    >
                        <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center mb-2">
                            <svg xmlns="http://www.w3.org/-svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-primary"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" /></svg>
                        </div>
                        <h2 className="text-2xl font-bold">Pular e ir para o chat</h2>
                        <p className="text-muted-foreground">
                            Quero apenas bater papo, pedir dicas soltas de animes e ir construindo meu gosto aos poucos interagindo com a IA.
                        </p>
                    </Card>
                </div>
            </div>
        </div>
    );
}
