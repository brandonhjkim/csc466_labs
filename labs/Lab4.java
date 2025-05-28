import java.io.*;
import java.util.*;

public class Lab4 {
    private static final double d = 0.9;
    private static HashSet<Integer> nodes = new HashSet<>();
    private static HashMap<Integer, ArrayList<Integer>> incomingEdges = new HashMap<>();
    private static HashMap<Integer, Integer> outDegree = new HashMap<>();
    private static HashMap<Integer, Double> pageRankOld = new HashMap<>();
    private static HashMap<Integer, Double> pageRankNew = new HashMap<>();

    public static void main(String[] args) {
        try {
            loadGraph("files/graph.txt"); 
            initializePageRanks();

            double distance;
            do {
                updatePageRanks();
                distance = findDistance(pageRankOld, pageRankNew);
                pageRankOld = new HashMap<>(pageRankNew); 
            } while (distance > 0.001);
            updatePageRanks();

            printTop20();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void loadGraph(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        HashSet<String> seenEdges = new HashSet<>();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split(",");
            if (parts.length < 3) continue;

            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[2]);

            String edgeKey = from + "->" + to;
            if (seenEdges.contains(edgeKey)) continue;
            seenEdges.add(edgeKey);

            nodes.add(from);
            nodes.add(to);

            incomingEdges.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
            outDegree.put(from, outDegree.getOrDefault(from, 0) + 1);
        }

        for (int node : nodes) {
            incomingEdges.putIfAbsent(node, new ArrayList<>());
            outDegree.putIfAbsent(node, 0);
        }

        reader.close();
    }

    private static void initializePageRanks() {
        double initialRank = 1.0 / nodes.size();
        for (int node : nodes) {
            pageRankOld.put(node, initialRank);
        }
    }

    private static void updatePageRanks() {
        int N = nodes.size();
        double total = 0.0;

        for (int node : nodes) {
            double rank = 0.0;
            for (int inNode : incomingEdges.get(node)) {
                int out = outDegree.get(inNode);
                if (out > 0) {
                    rank += pageRankOld.get(inNode) / out;
                }
            }
            pageRankNew.put(node, ((1.0 - d) / N) + (d * rank));
        }
        
        for (double pr : pageRankNew.values()) {
            total += pr;
        }
        for (int node : pageRankNew.keySet()) {
            pageRankNew.put(node, pageRankNew.get(node) / total);
        }

    }

    private static double findDistance(HashMap<Integer, Double> oldPR, HashMap<Integer, Double> newPR) {
        double sum = 0.0;
        for (int node : oldPR.keySet()) {
            sum += Math.abs(oldPR.get(node) - newPR.get(node));
        }
        return sum;
    }

    private static void printTop20() {
        List<Map.Entry<Integer, Double>> sorted = new ArrayList<>(pageRankNew.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Integer> top20 = new ArrayList<>();
        for (int i = 0; i < 20 && i < sorted.size(); i++) {
            top20.add(sorted.get(i).getKey());
        }

        System.out.println(top20);
    }
}
