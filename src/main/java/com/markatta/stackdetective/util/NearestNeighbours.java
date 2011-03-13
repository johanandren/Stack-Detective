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

import com.markatta.stackdetective.clustering.Distance;
import com.markatta.stackdetective.clustering.DistanceMatrix;
import com.markatta.stackdetective.clustering.NearestKQuery;
import com.markatta.stackdetective.distance.DefaultDistanceCalculator;
import com.markatta.stackdetective.distance.DistanceCalculator;
import com.markatta.stackdetective.distance.cost.IntelligentSubstitutionStrategy;
import com.markatta.stackdetective.model.StackTrace;
import java.util.List;
import java.util.Scanner;

/**
 * Small interactive console program for testing the nearest neighbour clustering.
 * 
 * @author johan
 */
public class NearestNeighbours {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: DistanceMatrix testFile1 testFile2 [testFile3 ...]");
            System.out.println(" where the testfile contains one stack trace each.");
            System.out.println(" Each stack trace found will be compared to ");
            System.out.println(" all other stack traces.");
            return;
        }

        List<StackTrace> stackTraces = FileParser.parseOnePerFile(args);

        
        DistanceCalculator calculator = new DefaultDistanceCalculator(new IntelligentSubstitutionStrategy());
        DistanceMatrix<StackTrace> distanceMatrix = new DistanceMatrix<StackTrace>(calculator);
        for (StackTrace stackTrace : stackTraces) {
            distanceMatrix.add(stackTrace);
        }
        
        for (int i = 0; i < stackTraces.size(); i++) {
            System.out.println((i + 1) + ": " + args[i]);
        }

        System.out.println("Use Ctrl-c to exit");
        Scanner scanner = new Scanner(System.in);

        NearestKQuery<StackTrace> query = new NearestKQuery<StackTrace>(distanceMatrix);
        while (true) {
            // print
            System.out.print("Number of nearest neighbours: ");
            int k = scanner.nextInt();
            System.out.print("To stack trace number (max " + stackTraces.size() + "): ");
            int stackTraceIndex = scanner.nextInt();
            List<Distance<StackTrace>> nearestK = query.getNearestK(stackTraces.get(stackTraceIndex - 1), k);
            
            System.out.print("Nearest: ");
            for (Distance<StackTrace> distance : nearestK) {
                System.out.print((stackTraces.indexOf(distance.getTo()) + 1) + " ");
            }
            System.out.print("\n");
        }

    }
}
