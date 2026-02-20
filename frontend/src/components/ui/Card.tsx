import React from 'react';

export function Card({ children, className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
    return (
        <div className={`rounded-xl border bg-card text-card-foreground shadow ${className || ''}`} {...props}>
            {children}
        </div>
    );
}

export function CardHeader({ children, className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
    return (
        <div className={`flex flex-col space-y-1.5 p-6 ${className || ''}`} {...props}>
            {children}
        </div>
    );
}

export function CardTitle({ children, className, ...props }: React.HTMLAttributes<HTMLHeadingElement>) {
    return (
        <h3 className={`text-2xl font-semibold leading-none tracking-tight ${className || ''}`} {...props}>
            {children}
        </h3>
    );
}

export function CardDescription({ children, className, ...props }: React.HTMLAttributes<HTMLParagraphElement>) {
    return (
        <p className={`text-sm text-muted-foreground ${className || ''}`} {...props}>
            {children}
        </p>
    );
}

export function CardContent({ children, className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
    return (
        <div className={`p-6 pt-0 ${className || ''}`} {...props}>
            {children}
        </div>
    );
}

export function CardFooter({ children, className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
    return (
        <div className={`flex items-center p-6 pt-0 ${className || ''}`} {...props}>
            {children}
        </div>
    );
}
