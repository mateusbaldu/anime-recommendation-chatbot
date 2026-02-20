import { Moon, Sun } from 'lucide-react';
import { Button } from './ui/Button';
import { useTheme } from './theme-provider';

export function ModeToggle() {
    const { theme, setTheme } = useTheme();

    const toggleTheme = () => {
        setTheme(theme === 'dark' ? 'light' : 'dark');
    };

    return (
        <Button
            onClick={toggleTheme}
            className="relative h-9 w-9 rounded-md border border-input bg-transparent text-foreground shadow-sm hover:bg-accent hover:text-accent-foreground p-0"
        >
            <Sun className="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
            <Moon className="absolute h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
            <span className="sr-only">Toggle theme</span>
        </Button>
    );
}
