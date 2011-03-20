/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markatta.stackdetective.util;

import com.markatta.stackdetective.clustering.DistanceMatrix;
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
public final class PrintDistanceMatrix {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: DistanceMatrix testFile1 [testFile2 ...]");
            System.out.println(" where the testfile contains one stack trace each.");
            System.out.println(" Each stack trace found will be compared to ");
            System.out.println(" all other stack traces.");
            return;
        }

        long startTime = System.currentTimeMillis();

        List<StackTrace> stackTraces = FileParser.parseOnePerFile(args);

        System.out.println("Found " + stackTraces.size() + " stack traces");

        DistanceCalculator calculator = new DefaultDistanceCalculator(new IntelligentSubstitutionStrategy());
        DistanceMatrix<StackTrace> distanceMatrix = new DistanceMatrix<StackTrace>(calculator);

        for (StackTrace stackTrace : stackTraces) {
            distanceMatrix.add(stackTrace);
        }


        // list files with numbers used in the matrix
        for (int i = 0; i < args.length; i++) {
            System.out.println((i + 1) + ". " + args[i]);
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
            StackTrace from = stackTraces.get(i);
            for (int j = 0; j < stackTraces.size(); j++) {
                StackTrace to = stackTraces.get(j);
                System.out.format("% 10d", distanceMatrix.getDistanceBetween(from, to));
                System.out.print("\t");
            }
            System.out.print("\n");
        }

        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Total execution time " + executionTime + " ms");


    }
}
