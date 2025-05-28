package DocumentClasses;

import java.util.Map;

public class CosineDistance implements DocumentDistance {
    @Override
    public double findDistance(TextVector query, TextVector document, DocumentCollection dc) {
        double dotProduct = 0.0;
        for (Map.Entry<String, Double> entry : query.getNormalizedVectorEntrySet()) {
            dotProduct += entry.getValue() * document.getNormalizedFrequency(entry.getKey());
        }
        double queryNorm = query.getL2Norm();
        double docNorm = document.getL2Norm(); 
        if (queryNorm == 0 || docNorm == 0) return 0.0;
        return dotProduct / (queryNorm * docNorm);
    }
}