package com.markatta.stackdetective.distance.levehnstein;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.markatta.stackdetective.model.Entry;

/**
 * Implementation of the levehnstein distance calculation algorithm. 
 * 
 * @author johan
 */
final class LevehnsteinDistanceCalculation {

    private static final Logger LOGGER = Logger.getLogger(LevehnsteinDistanceCalculation.class.getName());

    private final double[][] distance;

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
        distance = new double[aSize + 1][bSize + 1];
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
    	double deletion = distance[i - 1][j] + costStrategy.delete(entriesForA, i);
    	double insertion = distance[i][j - 1] + costStrategy.add(entriesForB, j);
    	double substitution = distance[i - 1][j - 1] + costStrategy.substitute(entriesForA, i, entriesForB, j);
    	double cost = getMinimum(deletion, insertion, substitution);

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
        if (LOGGER.isLoggable(Level.FINE)) {
            // print entire array
            StringBuilder builder = new StringBuilder("Distance array:\n");
            for (int i = 0; i <= aSize; i++) {
                for (int j = 0; j <= bSize; j++) {
                    builder.append(distance[i][j]);
                    builder.append("\t");
                }
                builder.append("\n");
            }
            LOGGER.log(Level.FINE, builder.toString());
        }
    }

    void printBackTrackToTraceLog() {
        if (LOGGER.isLoggable(Level.FINE)) {
            // print entire array
            StringBuilder builder = new StringBuilder("Operations array:\n");
            for (int i = 0; i <= aSize; i++) {
                for (int j = 0; j <= bSize; j++) {
                    builder.append(operations[i][j].name().substring(0, 1));
                    builder.append("(");
                    builder.append(Double.toString(distance[i][j]));
                    builder.append(")");
                    builder.append("\t");
                }
                builder.append("\n");
            }
            LOGGER.log(Level.FINE, builder.toString());
        }
    }

    double getResult() {
    	return distance[aSize][bSize];
    }

    
    private double getMinimum(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }
}
