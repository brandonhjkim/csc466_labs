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

    public ArrayList<Integer> findAllRows() {
        ArrayList<Integer> rows = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            rows.add(i);
        }
        return rows;
    }

    public int getCategoryAttribute() {
        return data[0].length - 1;
    }

    public double findProb(int[] row, int category) {
        int totalRows = data.length;
        double lambda = 1.0 / totalRows;
        double prob = 1.0;
        int catAttr = getCategoryAttribute();
        ArrayList<Integer> allRows = findAllRows();

        int categoryCount = 0;
        for (int[] datum : data) {
            if (datum[catAttr] == category) categoryCount++;
        }
        double prior = ((double) categoryCount + lambda) / (totalRows + lambda * getNumCategories());

        prob *= prior;

        for (int i = 0; i < catAttr; i++) {
            int matchCount = 0;
            int attrVal = row[i];
            for (int[] datum : data) {
                if (datum[catAttr] == category && datum[i] == attrVal) {
                    matchCount++;
                }
            }
            int uniqueVals = findDifferentValues(i, allRows).size();
            prob *= ((double) matchCount + lambda) / (categoryCount + lambda * uniqueVals);
        }

        return prob;
    }

    public int findCategory(int[] row) {
        HashSet<Integer> categories = new HashSet<>();
        int catAttr = getCategoryAttribute();
        for (int[] datum : data) {
            categories.add(datum[catAttr]);
        }

        double maxProb = -1;
        int bestCategory = -1;

        for (int category : categories) {
            double p = findProb(row, category);
            if (p > maxProb) {
                maxProb = p;
                bestCategory = category;
            }
        }

        return bestCategory;
    }

    private int getNumCategories() {
        HashSet<Integer> categories = new HashSet<>();
        for (int[] datum : data) {
            categories.add(datum[getCategoryAttribute()]);
        }
        return categories.size();
    }

}