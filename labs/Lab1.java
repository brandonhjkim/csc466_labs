package labs;

import DocumentClasses.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Lab1 {
    public static void main(String[] args) {
        DocumentCollection docs = new DocumentCollection("files/documents.txt", "document");

        String mostFreqWord = null;
        int highestFreq = 0;
        int totalDistinctWords = 0;
        int totalWordCount = 0;

        for (TextVector tv : docs.getDocuments()) {
            int tf = tv.getHighestRawFrequency();
            if (tf > highestFreq) {
                highestFreq = tf;
                mostFreqWord = tv.getMostFrequentWord();
            }
            totalDistinctWords += tv.getDistinctWordCount();
            totalWordCount += tv.getTotalWordCount();
        }

        System.out.println("Word = " + mostFreqWord);
        System.out.println("Frequency = " + highestFreq);
        System.out.println("Distinct Number of Words = " + totalDistinctWords);
        System.out.println("Total word count = " + totalWordCount);

        try (ObjectOutputStream os = new ObjectOutputStream(
                new FileOutputStream(new File("./files/docvector")))) {
            os.writeObject(docs);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
