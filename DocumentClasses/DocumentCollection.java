package DocumentClasses;

import java.io.*;
import java.util.*;

public class DocumentCollection implements Serializable {
    private HashMap<Integer, TextVector> documents = new HashMap<>();

    public DocumentCollection(String filePath, String type) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int id = -1;
            boolean body_check = false;
            StringBuilder body = new StringBuilder();
            int queryCounter = 1;

            while ((line = br.readLine()) != null) {
                if (line.startsWith(".I")) {
                    if (id != -1 && body.length() > 0) {
                        if (type.equalsIgnoreCase("query")) {
                            processDoc(queryCounter++, body.toString(), type);
                        } else {
                            processDoc(id, body.toString(), type);
                        }
                        body.setLength(0);
                    }
                    id = Integer.parseInt(line.substring(3).trim());
                    body_check = false;
                } else if (line.startsWith(".W")) {
                    body_check = true;
                } else if (body_check) {
                    body.append(" ").append(line);
                }
            }
            if (id != -1 && body.length() > 0) {
                if (type.equalsIgnoreCase("query")) {
                    processDoc(queryCounter++, body.toString(), type);
                } else {
                    processDoc(id, body.toString(), type);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processDoc(int docId, String text, String type) {
        TextVector vec = type.equalsIgnoreCase("query") ? new QueryVector() : new DocumentVector();
        String[] words = text.split("[^a-zA-Z]+");
        for (String word : words) {
            if (word.length() >= 2) {
                String lower = word.toLowerCase();
                if (!isNoiseWord(lower)) {
                    vec.add(lower);
                }
            }
        }
        documents.put(docId, vec);
    }

    private boolean isNoiseWord(String word) {
        return noiseWords.contains(word);
    }

    public void normalize(DocumentCollection dc) {
        for (TextVector tv : documents.values()) {
            tv.normalize(dc);
        }
    }

    public TextVector getDocumentById(int id) {
        return documents.get(id);
    }

    public double getAverageDocumentLength() {
        return documents.values().stream().mapToInt(TextVector::getTotalWordCount).average().orElse(0.0);
    }

    public int getSize() {
        return documents.size();
    }

    public Collection<TextVector> getDocuments() {
        return documents.values();
    }

    public Set<Map.Entry<Integer, TextVector>> getEntrySet() {
        return documents.entrySet();
    }

    public int getDocumentFrequency(String word) {
        int count = 0;
        for (TextVector tv : documents.values()) {
            if (tv.contains(word)) count++;
        }
        return count;
    }

    public static final String[] noiseWordArray = { "a", "about", "above", "all", "along", "also", "although", "am", "an",
        "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could",
        "couldn't", "did", "didn't", "do", "does", "doesn't", "e.g.", "either", "etc", "etc.", "even", "ever",
        "enough", "for", "from", "further", "get", "gets", "got", "had", "have", "hardly", "has", "hasn't",
        "having", "he", "hence", "her", "here", "hereby", "herein", "hereof", "hereon", "hereto", "herewith", "him",
        "his", "how", "however", "i", "i.e.", "if", "in", "into", "it", "it's", "its", "me", "more", "most", "mr",
        "my", "near", "nor", "now", "no", "not", "or", "on", "of", "onto", "other", "our", "out", "over", "really",
        "said", "same", "she", "should", "shouldn't", "since", "so", "some", "such", "than", "that", "the", "their",
        "them", "then", "there", "thereby", "therefore", "therefrom", "therein", "thereof", "thereon", "thereto",
        "therewith", "these", "they", "this", "those", "through", "thus", "to", "too", "under", "until", "unto",
        "upon", "us", "very", "was", "wasn't", "we", "were", "what", "when", "where", "whereby", "wherein",
        "whether", "which", "while", "who", "whom", "whose", "why", "with", "without", "would", "you", "your",
        "yours", "yes" };

    private static final Set<String> noiseWords = new HashSet<>(Arrays.asList(noiseWordArray));
}