package DocumentClasses;

import java.io.Serializable;
import java.util.*;

public abstract class TextVector implements Serializable {
    protected HashMap<String, Integer> rawVector = new HashMap<>();

    public void add(String word) {
        rawVector.put(word, rawVector.getOrDefault(word, 0) + 1);
    }

    public boolean contains(String word) {
        return rawVector.containsKey(word);
    }

    public int getRawFrequency(String word) {
        return rawVector.getOrDefault(word, 0);
    }

    public int getTotalWordCount() {
        return rawVector.values().stream().mapToInt(i -> i).sum();
    }

    public int getDistinctWordCount() {
        return rawVector.size();
    }

    public int getHighestRawFrequency() {
        return rawVector.values().stream().mapToInt(i -> i).max().orElse(0);
    }

    public String getMostFrequentWord() {
        return rawVector.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Set<Map.Entry<String, Integer>> getRawVectorEntrySet() {
        return rawVector.entrySet();
    }

    public double getL2Norm() {
        double sum = 0.0;
        for (double val : getNormalizedVectorEntrySet().stream().map(Map.Entry::getValue).toList()) {
            sum += val * val;
        }
        return Math.sqrt(sum);
    }

    public ArrayList<Integer> findClosestDocuments(DocumentCollection docs, DocumentDistance distanceAlg) {
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        for (Map.Entry<Integer, TextVector> entry : docs.getEntrySet()) {
            if (entry.getValue().getTotalWordCount() == 0) continue;
            double dist = distanceAlg.findDistance(this, entry.getValue(), docs);
            pq.add(new AbstractMap.SimpleEntry<>(entry.getKey(), dist));
        }
        ArrayList<Integer> topDocs = new ArrayList<>();
        for (int i = 0; i < 20 && !pq.isEmpty(); i++) {
            topDocs.add(pq.poll().getKey());
        }
        return topDocs;
    }

    public abstract Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet();
    public abstract void normalize(DocumentCollection dc);
    public abstract double getNormalizedFrequency(String word);
}