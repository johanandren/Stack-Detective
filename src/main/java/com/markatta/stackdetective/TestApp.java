package com.markatta.stackdetective;

import com.markatta.stackdetective.cost.FavourBeginStrategy;
import com.markatta.stackdetective.cost.IntelligentSubstitutionStrategy;
import com.markatta.stackdetective.filter.JBoss4JeeFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class TestApp {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: TestApp testFile");
            System.out.println(" where testfile contains stacktraces separated by empty lines");
            return;
        }


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
            StringBuilder builder = new StringBuilder();
            String line = null;
            List<StackTrace> stackTraces = new ArrayList<StackTrace>();

            while ((line = reader.readLine()) != null) {

                if (line.trim().length() == 0) {
                    // reached empty line
                    StackTraceTextParser parser = new StackTraceTextParser();
                    StackTrace trace = parser.parse(builder);
                    stackTraces.add(trace);
                    builder.setLength(0);
                } else {
                    builder.append(line);
                    builder.append("\n");
                }

            }

            System.out.println("Found " + stackTraces.size() + " stack traces in the file " + args[0]);

            int[][] scores = new int[stackTraces.size()][stackTraces.size()];

            DistanceCalculator calculator = new DefaultDistanceCalculator(new IntelligentSubstitutionStrategy());
            
            // calculate
            for (int i = 0; i < stackTraces.size(); i++) {
                for (int j = 0; j < stackTraces.size(); j++) {
                    scores[i][j] = calculator.calculateDistance(stackTraces.get(i), stackTraces.get(j));
                }

            }

            // print
            System.out.println("Score for each compared to each of the others and itself (low is more alike, 0 is identical):");
            System.out.print("   \t");
            for (int i = 0; i < stackTraces.size(); i++) {
                System.out.format("% 10d\t", i + 1);
            }
            System.out.print("\n");
            for (int i = 0; i < stackTraces.size(); i++) {
                System.out.format("% 3d\t", i + 1);
                for (int j = 0; j < stackTraces.size(); j++) {
                    System.out.format("% 10d", scores[i][j]);
                    System.out.print("\t");
                }
                System.out.print("\n");
            }


        } catch (Exception ex) {
            throw new RuntimeException("Error running test app", ex);
        }
    }
}
