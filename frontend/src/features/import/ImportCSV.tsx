import React, { useState } from 'react';
import { Button } from '../../components/ui/Button';

export function ImportCSV() {
    const [file, setFile] = useState<File | null>(null);

    const handleImport = () => {
        if (!file) return;
        console.log("Importing file...", file.name);
        // Logic to send file to backend
    };

    return (
        <div className="p-6 border rounded-lg max-w-md mx-auto space-y-4 bg-card">
            <h2 className="text-xl font-bold">Import from Letterboxd/MAL</h2>
            <p className="text-sm text-muted-foreground">Upload your CSV tracking file to get personalized recommendations based on your taste.</p>

            <input
                type="file"
                accept=".csv"
                className="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-primary file:text-primary-foreground hover:file:bg-primary/90"
                onChange={(e) => setFile(e.target.files?.[0] || null)}
            />
            <Button disabled={!file} onClick={handleImport} className="w-full">
                Upload Data
            </Button>
        </div>
    );
}
