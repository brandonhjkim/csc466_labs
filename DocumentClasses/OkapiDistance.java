package DocumentClasses;

import java.util.Map;

public class OkapiDistance implements DocumentDistance {
    private final double k1 = 1.2;
    private final double b = 0.75;
    private final double k2 = 100;

    @Override
    public double findDistance(TextVector query, TextVector document, DocumentCollection dc) {
        double score = 0.0;
        double avgdl = dc.getAverageDocumentLength();
        double dl = document.getTotalWordCount();

        for (Map.Entry<String, Integer> entry : query.getRawVectorEntrySet()) {
            String term = entry.getKey();
            int qf = entry.getValue();
            int f = document.getRawFrequency(term);
            int df = dc.getDocumentFrequency(term);

            if (f == 0 || df == 0) continue;

            double idf = Math.log((dc.getSize() - df + 0.5) / (df + 0.5));
            double tfdoc = ((k1 + 1) * f) / (k1 * (1 - b + (b * (dl / avgdl))) + f);
            double tfquery = ((k2 + 1) * qf) / (k2 + qf);
            score += idf * tfdoc * tfquery;
        }
        return score;
    }
}