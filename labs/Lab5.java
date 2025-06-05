import java.io.*;
import java.util.*;

import AssociationRules.ItemSet; 

public class Lab5 {
    static ArrayList<ItemSet> transactions = new ArrayList<>();
    static ArrayList<Integer> items = new ArrayList<>();
    static HashMap<Integer, ArrayList<ItemSet>> frequentItemSet = new HashMap<>();
    static int minSupport;

    public static void main(String[] args) throws IOException {
        process("files/shopping_data.txt");

        minSupport = (int) Math.ceil(transactions.size() * 0.01);

        findFrequentSingleItemSets();
        int k = 2;
        while (findFrequentItemSets(k)) {
            k++;
        }

        System.out.println(frequentItemSet);
    }

    public static void process(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;

        Set<Integer> uniqueItems = new HashSet<>();
        while ((line = br.readLine()) != null) {
            String[] tokens = line.trim().split(",");  // Split by comma
            ArrayList<Integer> itemsInTransaction = new ArrayList<>();
            for (int i = 1; i < tokens.length; i++) { // Skip transaction number
                int item = Integer.parseInt(tokens[i].trim());
                itemsInTransaction.add(item);
                uniqueItems.add(item);
            }
            transactions.add(new ItemSet(itemsInTransaction));
        }
        br.close();

        items.addAll(uniqueItems);
        Collections.sort(items);
    }

    public static void findFrequentSingleItemSets() {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (ItemSet t : transactions) {
            for (int item : t.getItems()) {
                countMap.put(item, countMap.getOrDefault(item, 0) + 1);
            }
        }

        ArrayList<ItemSet> frequent = new ArrayList<>();
        for (int item : countMap.keySet()) {
            if (countMap.get(item) >= minSupport) {
                frequent.add(new ItemSet(new ArrayList<>(Arrays.asList(item))));
            }
        }
        frequentItemSet.put(1, frequent);
    }

    public static boolean findFrequentItemSets(int k) {
        ArrayList<ItemSet> prevLevel = frequentItemSet.get(k - 1);
        Set<ItemSet> candidates = new HashSet<>();

        for (int i = 0; i < prevLevel.size(); i++) {
            for (int j = i + 1; j < prevLevel.size(); j++) {
                List<Integer> items1 = prevLevel.get(i).getItems();
                List<Integer> items2 = prevLevel.get(j).getItems();

                if (canJoin(items1, items2)) {
                    ArrayList<Integer> merged = new ArrayList<>(items1);
                    merged.add(items2.get(items2.size() - 1));
                    ItemSet candidate = new ItemSet(merged);
                    if (hasFrequentSubsets(candidate, k - 1)) {
                        candidates.add(candidate);
                    }
                }
            }
        }

        ArrayList<ItemSet> frequent = new ArrayList<>();
        for (ItemSet candidate : candidates) {
            if (isFrequent(candidate)) {
                frequent.add(candidate);
            }
        }

        if (frequent.isEmpty()) return false;
        frequentItemSet.put(k, frequent);
        return true;
    }

    public static boolean canJoin(List<Integer> a, List<Integer> b) {
        for (int i = 0; i < a.size() - 1; i++) {
            if (!a.get(i).equals(b.get(i))) return false;
        }
        return a.get(a.size() - 1) < b.get(b.size() - 1);
    }

    public static boolean hasFrequentSubsets(ItemSet itemSet, int k) {
        List<Integer> items = itemSet.getItems();
        for (int i = 0; i < items.size(); i++) {
            ArrayList<Integer> subset = new ArrayList<>(items);
            subset.remove(i);
            if (!frequentItemSet.get(k).contains(new ItemSet(subset))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFrequent(ItemSet itemSet) {
        int count = 0;
        for (ItemSet t : transactions) {
            if (t.containsAll(itemSet.getItems())) {
                count++;
            }
        }
        return count >= minSupport;
    }
}