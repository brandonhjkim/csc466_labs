import java.io.*;
import java.util.*;

import AssociationRules.Rule; 
import AssociationRules.ItemSet; 

public class Lab6 {
    static ArrayList<ItemSet> transactions = new ArrayList<>();
    static ArrayList<Integer> items = new ArrayList<>();
    static HashMap<Integer, ArrayList<ItemSet>> frequentItemSet = new HashMap<>();
    static ArrayList<Rule> rules = new ArrayList<>();
    static int minSupport;

    public static void main(String[] args) throws IOException {
        process("files/shopping_data.txt");

        minSupport = (int) Math.ceil(transactions.size() * 0.01);

        findFrequentSingleItemSets();
        int k = 2;
        while (findFrequentItemSets(k)) {
            k++;
        }

        generateRules();
        System.out.println(rules);
    }

    public static void process(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        Set<Integer> uniqueItems = new HashSet<>();

        while ((line = br.readLine()) != null) {
            String[] tokens = line.trim().split(",");
            ArrayList<Integer> itemsInTransaction = new ArrayList<>();

            for (int i = 1; i < tokens.length; i++) {
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

        for (ItemSet transaction : transactions) {
            for (int item : transaction.getItems()) {
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
        ArrayList<ItemSet> prev = frequentItemSet.get(k - 1);
        Set<ItemSet> candidates = new HashSet<>();

        for (int i = 0; i < prev.size(); i++) {
            for (int j = i + 1; j < prev.size(); j++) {
                List<Integer> items1 = prev.get(i).getItems();
                List<Integer> items2 = prev.get(j).getItems();

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
        for (ItemSet c : candidates) {
            if (isFrequent(c)) {
                frequent.add(c);
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

    public static void generateRules() {
        for (int k : frequentItemSet.keySet()) {
            if (k <= 1) continue;
            for (ItemSet itemset : frequentItemSet.get(k)) {
                rules.addAll(split(itemset));
            }
        }
    }

    public static ArrayList<Rule> split(ItemSet itemSet) {
        ArrayList<Rule> result = new ArrayList<>();
        List<Integer> items = itemSet.getItems();
        int n = items.size();

        for (int i = 1; i < (1 << n) - 1; i++) {
            ArrayList<Integer> left = new ArrayList<>();
            ArrayList<Integer> right = new ArrayList<>();

            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0)
                    left.add(items.get(j));
                else
                    right.add(items.get(j));
            }

            if (!left.isEmpty() && !right.isEmpty()) {
                Rule r = new Rule(new ItemSet(left), new ItemSet(right));
                if (isMinConfidenceMet(r)) {
                    result.add(r);
                }
            }
        }

        return result;
    }

    public static boolean isMinConfidenceMet(Rule r) {
        int leftCount = 0, bothCount = 0;
        for (ItemSet t : transactions) {
            if (t.containsAll(r.getLeft().getItems())) {
                leftCount++;
                if (t.containsAll(r.getRight().getItems())) {
                    bothCount++;
                }
            }
        }
        return leftCount > 0 && ((double) bothCount / leftCount) >= 0.99;
    }
}