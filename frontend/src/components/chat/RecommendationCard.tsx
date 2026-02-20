import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Badge } from '../ui/Badge';

export interface RecommendationCardProps {
    data: {
        title: string;
        genres: string[];
        reason: string;
    };
}

export function RecommendationCard({ data }: RecommendationCardProps) {
    return (
        <Card className="my-4 border-primary/30 bg-primary/5 shadow-md">
            <CardHeader className="pb-3">
                <CardTitle className="text-xl text-primary">{data.title}</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
                <div className="flex flex-wrap gap-2">
                    {data.genres.map((genre, index) => (
                        <Badge key={index} variant="secondary">
                            {genre}
                        </Badge>
                    ))}
                </div>
                <div className="text-sm text-muted-foreground leading-relaxed">
                    <p><strong>Porquê:</strong> {data.reason}</p>
                </div>
            </CardContent>
        </Card>
    );
}
