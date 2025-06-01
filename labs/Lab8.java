import java.util.*;
import java.io.*;

import SLClasses.Matrix;

public class Lab8 {
    public static void main(String[] args) {
        try {
            int[][] data = readInput("files/data.txt");
            Matrix matrix = new Matrix(data);
            int[] userRow = getCustomerInput();

            HashSet<Integer> categories = new HashSet<>();
            for (int[] row : data) {
                categories.add(row[matrix.getCategoryAttribute()]);
            }

            for (int category : categories) {
                double prob = matrix.findProb(userRow, category);
                System.out.println("For value " + category + ": Probability is: " + prob);
            }

            int predicted = matrix.findCategory(userRow);
            System.out.println("Expected category: " + predicted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[][] readInput(String filename) throws IOException {
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
            System.out.println("Error reading file: " + e.getMessage());
        }
        return list.toArray(new int[0][]);
    }

    public static int[] getCustomerInput() {
        Scanner scanner = new Scanner(System.in);
        int[] input = new int[4];
        for (int i = 0; i < 4; i++) {
            System.out.print("Enter value for attribute " + i + ": ");
            input[i] = scanner.nextInt();
        }
        return input;
    }
}
