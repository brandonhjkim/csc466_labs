import java.io.*;
import java.util.*;

import SLClasses.Matrix;

public class Lab7 {
    public static void main(String[] args) {
        int[][] data = process("files/data.txt");
        Matrix matrix = new Matrix(data);
        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Integer> attributes = new ArrayList<>();
        for (int i = 0; i < data.length; i++) rows.add(i);
        for (int i = 0; i < data[0].length - 1; i++) attributes.add(i);
        printDecisionTree(matrix, data, attributes, rows, 0);
    }

    public static int[][] process(String filename) {
        ArrayList<int[]> list = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNextLine()) {
                String[] tokens = sc.nextLine().split(",");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    if (i < tokens.length - 1)
                        row[i] = (int) Double.parseDouble(tokens[i]);
                    else
                        row[i] = Integer.parseInt(tokens[i]);
                }
                list.add(row);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list.toArray(new int[0][]);
    }

    public static void printDecisionTree(Matrix matrix, int[][] data, ArrayList<Integer> attributes, ArrayList<Integer> rows, int level) {
        
        boolean allSameClass = rows.stream().map(r -> data[r][data[0].length - 1]).distinct().count() == 1;

        if (allSameClass) {
            int value = data[rows.get(0)][data[0].length - 1];
            System.out.println("  ".repeat(level) + "value = " + value);
            return;
        }                                    

        if (attributes.isEmpty()) {
            int value = matrix.findMostCommonValue(rows);
            System.out.println("  ".repeat(level) + "value = " + value);
            return;
        }

        int bestAttr = -1;
        double bestIGR = 0.0;
        for (int attr : attributes) {
            double igr = matrix.computeIGR(attr, rows);
            if (igr > bestIGR) {
                bestIGR = igr;
                bestAttr = attr;
            }
        }

        if (bestIGR < 0.01 || bestAttr == -1) {
            int value = matrix.findMostCommonValue(rows);
            System.out.println("  ".repeat(level) + "value = " + value);
            return;
        }

        HashMap<Integer, ArrayList<Integer>> splits = matrix.split(bestAttr, rows);
        ArrayList<Integer> newAttributes = new ArrayList<>(attributes);
        newAttributes.remove(Integer.valueOf(bestAttr));

        for (Map.Entry<Integer, ArrayList<Integer>> entry : splits.entrySet()) {
            ArrayList<Integer> childRows = entry.getValue();
            System.out.println("  ".repeat(level) + "When attribute " + (bestAttr+1) + " has value " + entry.getKey());

            boolean childPure = childRows.stream().map(r -> data[r][data[0].length - 1]).distinct().count() == 1;

            if (childPure) {
                int val = data[childRows.get(0)][data[0].length - 1];
                System.out.println("  ".repeat(level + 1) + "value = " + val);
            } else {
                printDecisionTree(matrix, data, newAttributes, childRows, level + 1);
            }
        }
    }
}
