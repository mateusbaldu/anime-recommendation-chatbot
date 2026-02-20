import { useNavigate } from "react-router-dom";
import { GoogleLogin } from '@react-oauth/google';
import type { CredentialResponse } from '@react-oauth/google';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { authService } from "@/services/authService";

export function LoginPage() {
    const navigate = useNavigate();

    const handleGoogleSuccess = async (credentialResponse: CredentialResponse) => {
        try {
            const googleToken = credentialResponse.credential;

            if (!googleToken) {
                console.error("No credential received from Google");
                return;
            }

            // Send to backend (or store directly as fallback)
            await authService.loginWithGoogle(googleToken);

            // Redirect to onboarding
            navigate("/onboarding");
        } catch (error) {
            console.error("Erro ao autenticar com o backend", error);
        }
    };

    const handleGuestLogin = () => {
        const guestId = crypto.randomUUID();
        localStorage.setItem('guestSessionId', guestId);
        navigate("/onboarding");
    };

    return (
        <div className="flex items-center justify-center min-h-[80vh]">
            <Card className="w-[380px] shadow-lg">
                <CardHeader className="text-center">
                    <CardTitle className="text-2xl text-primary">
                        AnimeRec AI
                    </CardTitle>
                    <CardDescription>
                        Faça login para receber recomendações personalizadas de anime e filmes.
                    </CardDescription>
                </CardHeader>
                <CardContent className="flex flex-col items-center gap-4 py-4">
                    <GoogleLogin
                        onSuccess={handleGoogleSuccess}
                        onError={() => console.log('Falha no Login do Google')}
                        theme="filled_blue"
                        shape="rectangular"
                        size="large"
                        text="signin_with"
                    />

                    {/* Divider */}
                    <div className="flex items-center w-full gap-3 my-2">
                        <div className="flex-1 h-px bg-border" />
                        <span className="text-xs text-muted-foreground uppercase">ou</span>
                        <div className="flex-1 h-px bg-border" />
                    </div>

                    <Button
                        variant="outline"
                        className="w-full"
                        onClick={handleGuestLogin}
                    >
                        Testar sem logar
                    </Button>
                </CardContent>
            </Card>
        </div>
    );
}

