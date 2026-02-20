import { Button } from '../../components/ui/Button';

export function LoginGoogle() {
    const handleLogin = () => {

        console.log("Logging in with Google...");
    };

    return (
        <div className="flex flex-col items-center justify-center p-6 space-y-4">
            <Button onClick={handleLogin} className="w-full max-w-sm">
                Sign in with Google
            </Button>
        </div>
    );
}
