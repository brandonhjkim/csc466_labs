package SLClasses;

import java.util.*; 

public class Matrix {
    private int[][] data;

    public Matrix(int[][] data) {
        this.data = data;
    }

    private HashSet<Integer> findDifferentValues(int attribute, ArrayList<Integer> rows) {
        HashSet<Integer> values = new HashSet<>();
        for (int row : rows) {
            values.add(data[row][attribute]);
        }
        return values;
    }

    private ArrayList<Integer> findRows(int attribute, int value, ArrayList<Integer> rows) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int row : rows) {
            if (data[row][attribute] == value) result.add(row);
        }
        return result;
    }

    private double log2(double number) {
        return Math.log(number) / Math.log(2);
    }

    private double findEntropy(ArrayList<Integer> rows) {
        HashMap<Integer, Integer> classCounts = new HashMap<>();
        for (int row : rows) {
            int label = data[row][data[0].length - 1];
            classCounts.put(label, classCounts.getOrDefault(label, 0) + 1);
        }
        double entropy = 0.0;
        int total = rows.size();
        for (int count : classCounts.values()) {
            double p = (double) count / total;
            entropy -= p * log2(p);
        }
        return entropy;
    }

    private double findEntropy(int attribute, ArrayList<Integer> rows) {
        HashSet<Integer> values = findDifferentValues(attribute, rows);
        double totalEntropy = 0.0;
        int totalRows = rows.size();
        for (int value : values) {
            ArrayList<Integer> subset = findRows(attribute, value, rows);
            double weight = (double) subset.size() / totalRows;
            totalEntropy += weight * findEntropy(subset);
        }
        return totalEntropy;
    }

    private double findGain(int attribute, ArrayList<Integer> rows) {
        return findEntropy(rows) - findEntropy(attribute, rows);
    }

    public double computeIGR(int attribute, ArrayList<Integer> rows) {
        double gain = findGain(attribute, rows);
        double splitInfo = 0.0;
        HashSet<Integer> values = findDifferentValues(attribute, rows);
        int totalRows = rows.size();
        for (int value : values) {
            ArrayList<Integer> subset = findRows(attribute, value, rows);
            double p = (double) subset.size() / totalRows;
            splitInfo -= p * log2(p);
        }
        return (splitInfo == 0) ? 0 : gain / splitInfo;
    }

    public int findMostCommonValue(ArrayList<Integer> rows) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int row : rows) {
            int label = data[row][data[0].length - 1];
            counts.put(label, counts.getOrDefault(label, 0) + 1);
        }
        return Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public HashMap<Integer, ArrayList<Integer>> split(int attribute, ArrayList<Integer> rows) {
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        for (int row : rows) {
            int key = data[row][attribute];
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(row);
        }
        return map;
    }
} 
