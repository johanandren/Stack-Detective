package com.markatta.stackdetective.util;

import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.distance.DefaultDistanceCalculator;
import com.markatta.stackdetective.distance.DistanceCalculator;
import com.markatta.stackdetective.distance.cost.IntelligentSubstitutionStrategy;
import java.util.List;

/**
 * Small example app that reads stack traces from files listed as arguments and
 * compares every found stack trace to every other stack trace and prints a 
 * matrix of distances between them.
 */
public final class DistanceMatrix {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: DistanceMatrix testFile1 [testFile2 ...]");
            System.out.println(" where the testfile contains one stack trace each.");
            System.out.println(" Each stack trace found will be compared to ");
            System.out.println(" all other stack traces.");
            return;
        }

        List<StackTrace> stackTraces = FileParser.parseOnePerFile(args);

        System.out.println("Found " + stackTraces.size() + " stack traces");

        int[][] scores = new int[stackTraces.size()][stackTraces.size()];

        DistanceCalculator calculator = new DefaultDistanceCalculator(new IntelligentSubstitutionStrategy());

        // calculate
        for (int i = 0; i < stackTraces.size(); i++) {
            for (int j = 0; j < stackTraces.size(); j++) {
                scores[i][j] = calculator.calculateDistance(stackTraces.get(i), stackTraces.get(j));
            }

        }

        for (int i = 0; i < args.length; i++) {
            System.out.println(i + ". " + args[i]);
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


    }
}
