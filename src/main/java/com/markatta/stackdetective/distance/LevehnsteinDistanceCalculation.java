package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.distance.cost.DistanceCostStrategy;
import com.markatta.stackdetective.model.Entry;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Implementation of the levehnstein distance calculation algorithm. 
 * 
 * @author johan
 */
final class LevehnsteinDistanceCalculation {

    private static final Logger LOGGER = Logger.getLogger(LevehnsteinDistanceCalculation.class);

    private final int[][] distance;

    private final List<Entry> entriesForA;

    private final List<Entry> entriesForB;

    private final int aSize;

    private final int bSize;

    private final DistanceCostStrategy costStrategy;

    private final Operation[][] operations;

    LevehnsteinDistanceCalculation(List<Entry> entriesForA, List<Entry> entriesForB, DistanceCostStrategy costStrategy) {
        this.entriesForA = entriesForA;
        this.entriesForB = entriesForB;
        aSize = entriesForA.size();
        bSize = entriesForB.size();
        distance = new int[aSize + 1][bSize + 1];
        operations = new Operation[aSize + 1][bSize + 1];

        this.costStrategy = costStrategy;
    }

    void calculateDistance() {
        setupDefaultCosts();
        floodFillDistances();
    }

    private void setupDefaultCosts() {
        distance[0][0] = 0;
        operations[0][0] = Operation.NONE;

        // [0][*] contains the cost to add any sub-stracktrace of b to an empty trace a
        for (int bIndex = 1; bIndex <= bSize; bIndex++) {
            distance[0][bIndex] = costStrategy.add(entriesForB, bIndex);
            operations[0][bIndex] = Operation.INSERT;
        }
        // [*][0] contains the cost to add any sub-stacktrace of a to an empty trace b
        for (int i = 1; i <= aSize; i++) {
            distance[i][0] = distance[i - 1][0] + costStrategy.add(entriesForA, i);
            operations[i][0] = Operation.INSERT;
        }
    }

    /**
     * Flood fill the distance array so that any position [i,j] contains
     * the minimum distance between the substring 0-i from entry list a and
     * 0-j from entry list b
     */
    private void floodFillDistances() {

        for (int i = 1; i <= aSize; i++) {
            for (int j = 1; j <= bSize; j++) {
                calculateCostFor(i, j);
            }
        }
    }

    /**
     * Must be called in order as it relies on the values for lower i and j to
     * be available.
     * @return The minimum distance between the substring 0-i from entry list a
     *         and the substring 0-j from entry list b.
     */
    private void calculateCostFor(int i, int j) {
        // choose the cheapest operation
        int deletion = distance[i - 1][j] + costStrategy.delete(entriesForA, i);
        int insertion = distance[i][j - 1] + costStrategy.add(entriesForB, j);
        int substitution = distance[i - 1][j - 1] + costStrategy.substitute(entriesForA, i, entriesForB, j);
        int cost = getMinimum(deletion, insertion, substitution);

        // backtrack logging
        if (deletion == cost) {
            operations[i][j] = Operation.DELETE;
        } else if (insertion == cost) {
            operations[i][j] = Operation.INSERT;
        } else {
            operations[i][j] = Operation.SUBSTITUTE;
        }

        distance[i][j] = cost;
    }

    void printEntireArrayToTraceLog() {
        if (LOGGER.isTraceEnabled()) {
            // print entire array
            StringBuilder builder = new StringBuilder("Distance array:\n");
            for (int i = 0; i <= aSize; i++) {
                for (int j = 0; j <= bSize; j++) {
                    builder.append(distance[i][j]);
                    builder.append("\t");
                }
                builder.append("\n");
            }
            LOGGER.trace(builder.toString());
        }
    }

    void printBackTrackToTraceLog() {
        if (LOGGER.isTraceEnabled()) {
            // print entire array
            StringBuilder builder = new StringBuilder("Operations array:\n");
            for (int i = 0; i <= aSize; i++) {
                for (int j = 0; j <= bSize; j++) {
                    builder.append(operations[i][j].name().substring(0, 1));
                    builder.append("(");
                    builder.append(Integer.toString(distance[i][j]));
                    builder.append(")");
                    builder.append("\t");
                }
                builder.append("\n");
            }
            LOGGER.trace(builder.toString());
        }
    }

    int getResult() {
        return distance[aSize][bSize];
    }

    List<BackTrackElement> getBackTrack() {
        List<BackTrackElement> backTrack = new ArrayList<BackTrackElement>(Math.max(aSize, bSize));

        int i = 0;
        int j = 0;

        while (i < aSize || j < bSize) {
            int below;
            if (i == aSize) {
                below = Integer.MAX_VALUE;
            } else {
                below = distance[i + 1][j];
            }
            int diagonal;
            if (i == aSize || j == bSize) {
                diagonal = Integer.MAX_VALUE;
            } else {
                diagonal = distance[i + 1][j + 1];
            }
            int right;
            if (j == bSize) {
                right = Integer.MAX_VALUE;
            } else {
                right = distance[i][j + 1];
            }
            int min = getMinimum(below, diagonal, right);
            if (below == min) {
                i++;
            } else if (right == min) {
                j++;
            } else {
                i++;
                j++;
            }

            backTrack.add(new BackTrackElement(operations[i][j], distance[i][j], i, j));
        }



        return backTrack;
    }

    private int getMinimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
}
