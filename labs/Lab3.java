package labs;

import DocumentClasses.*;
import java.io.*;
import java.util.*;

public class Lab3 {
    public static void main(String[] args) {
        DocumentCollection documents = null;
        DocumentCollection queries = null;

        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("./files/docvector"))) {
            documents = (DocumentCollection) is.readObject();
        } catch (Exception e) {
            System.out.println("Error loading documents: " + e);
        }

        queries = new DocumentCollection("./files/queries.txt", "query");
        documents.normalize(documents);
        queries.normalize(documents);

        DocumentDistance cosine = new CosineDistance();
        HashMap<Integer, ArrayList<Integer>> cosineResults = new HashMap<>();
        for (Map.Entry<Integer, TextVector> entry : queries.getEntrySet()) {
            int queryId = entry.getKey();
            ArrayList<Integer> top = entry.getValue().findClosestDocuments(documents, cosine);
            cosineResults.put(queryId, top);
        }

        DocumentDistance okapi = new OkapiDistance();
        HashMap<Integer, ArrayList<Integer>> okapiResults = new HashMap<>();
        for (int queryId = 1; queryId <= 20; queryId++) {
            TextVector qv = queries.getDocumentById(queryId);
            if (qv != null) {
                ArrayList<Integer> topDocs = qv.findClosestDocuments(documents, okapi);
                okapiResults.put(queryId, topDocs);
            }
        }

        HashMap<Integer, Set<Integer>> humanJudgement = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("./files/human_judgement.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\s+");
                if (parts.length == 3) {
                    int qid = Integer.parseInt(parts[0]);
                    int docid = Integer.parseInt(parts[1]);
                    int relevance = Integer.parseInt(parts[2]);
                    if (relevance >= 1 && relevance <= 3) {
                        humanJudgement.putIfAbsent(qid, new HashSet<>());
                        humanJudgement.get(qid).add(docid);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading human judgment: " + e);
        }

        System.out.println("Cosine MAP = " + computeMAP(humanJudgement, cosineResults));
        System.out.println("Okapi MAP = " + computeMAP(humanJudgement, okapiResults));
    }

    public static double computeMAP(HashMap<Integer, Set<Integer>> groundTruth,
                                    HashMap<Integer, ArrayList<Integer>> predicted) {
        double totalMap = 0.0;

        for (int i = 1; i <= 20; i++) {
            Set<Integer> relevant = groundTruth.getOrDefault(i, new HashSet<>());
            ArrayList<Integer> retrieved = predicted.getOrDefault(i, new ArrayList<>());
            double avgPrec = 0.0;
            int hit = 0;

            for (int rank = 0; rank < retrieved.size() && rank < 20; rank++) {
                if (relevant.contains(retrieved.get(rank))) {
                    hit++;
                    avgPrec += (double) hit / (rank + 1);
                }
            }

            if (!relevant.isEmpty()) {
                totalMap += avgPrec / relevant.size();
            }
        }

        return totalMap / 20.0;
    }
}