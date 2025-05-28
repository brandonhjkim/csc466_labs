package DocumentClasses;

import java.util.*;

public class QueryVector extends TextVector {
    private HashMap<String, Double> normalizedVector = new HashMap<>();

    @Override
    public Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet() {
        return normalizedVector.entrySet();
    }

    @Override
    public void normalize(DocumentCollection dc) {
        int maxFreq = getHighestRawFrequency();
        for (Map.Entry<String, Integer> entry : getRawVectorEntrySet()) {
            String word = entry.getKey();
            int freq = entry.getValue();
            double tf = (double) 0.5 + (0.5 * freq / maxFreq);
            int df = dc.getDocumentFrequency(word);
            double idf = df == 0 ? 0.0 : Math.log((double) dc.getSize() / df);
            normalizedVector.put(word, tf * idf);
        }
    }

    @Override
    public double getNormalizedFrequency(String word) {
        return normalizedVector.getOrDefault(word, 0.0);
    }
}