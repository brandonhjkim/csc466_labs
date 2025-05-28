package labs;

import DocumentClasses.*;
import java.io.*;
import java.util.*;

public class Lab2 {
    public static DocumentCollection documents;
    public static DocumentCollection queries;

    public static void main(String[] args) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("./files/docvector"))) {
            documents = (DocumentCollection) is.readObject();
        } catch (Exception e) {
            System.out.println(e); 
        }

        queries = new DocumentCollection("./files/queries.txt", "query");
        documents.normalize(documents);
        queries.normalize(documents);

        DocumentDistance distanceAlg = new CosineDistance();
        HashMap<Integer, ArrayList<Integer>> topDocsPerQuery = new HashMap<>();

        for (Map.Entry<Integer, TextVector> entry : queries.getEntrySet()) {
            int queryId = entry.getKey();
            ArrayList<Integer> top = entry.getValue().findClosestDocuments(documents, distanceAlg);
            topDocsPerQuery.put(queryId, top);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("./files/topDocsPerQuery.csv"))) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : topDocsPerQuery.entrySet()) {
                StringBuilder line = new StringBuilder();
                line.append(entry.getKey());
                for (int docId : entry.getValue()) {
                    line.append(",").append(docId);
                }
                writer.println(line);
            }
            System.out.println("Top documents saved to topDocsPerQuery.csv");
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }
}