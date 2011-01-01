package com.markatta.stackdetective;

import com.markatta.stackdetective.cost.DistanceCostStrategy;
import com.markatta.stackdetective.filter.EntryFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Uses a weighted variation of the levenshtein distance calculation algorithm
 * that will favour similiarities in the beginning of the stack traces.
 * 
 * @author johan
 */
public class DefaultDistanceCalculator implements DistanceCalculator {

    private final static int DELETION_COST = 100;

    private final static int INSERTION_COST = DELETION_COST;

    private final DistanceCostStrategy costStrategy;

    private EntryFilter filter = null;

    public DefaultDistanceCalculator(DistanceCostStrategy costStrategy) {
        this.costStrategy = costStrategy;
    }

    public void setFilter(EntryFilter filter) {
        this.filter = filter;
    }

    public int calculateDistance(StackTrace a, StackTrace b) {
        List<SegmentEntry> entriesForA = a.flatten();
        List<SegmentEntry> entriesForB = b.flatten();

        if (filter != null) {
            applyFilter(entriesForA);
            applyFilter(entriesForB);
        }

        int aSize = entriesForA.size();
        int bSize = entriesForB.size();
        int[][] distance = new int[aSize + 1][bSize + 1];
        distance[0][0] = 0;
        for (int bIndex = 1; bIndex <= bSize; bIndex++) {
            distance[0][bIndex] = costStrategy.add(entriesForB, bIndex);
        }

        for (int i = 1; i <= aSize; i++) {
            distance[i][0] = distance[i - 1][0] + costStrategy.add(entriesForA, i);

            for (int j = 1; j <= bSize; j++) {

                int deletion = distance[i - 1][j] + costStrategy.delete(entriesForA, i);
                int insertion = distance[i][j - 1] + costStrategy.add(entriesForB, j);
                int substitution = distance[i - 1][j - 1] + costStrategy.substitute(entriesForA, i, entriesForB, j);
                int min = Math.min(deletion, insertion);
                min = Math.min(min, substitution);

                distance[i][j] = min;
            }
        }

        return distance[aSize][bSize];
    }

    private int substitutionCost(SegmentEntry entryA, SegmentEntry entryB, int posA, int posB) {
        int cost = 0;
        if (!entryA.getClassName().equals(entryB.getClassName())) {
            // different classes
            cost = 1000;
        } else if (!entryA.getMethodName().equals(entryB.getMethodName())) {
            // same class, different methods
            cost = 100;
        } else if (entryA.getLineNumber() != entryB.getLineNumber()) {
            // same method and class but different line numbers
            // should indicate different versions of the same library etc.
            cost = 1;
        } else {
            // same same
            cost = 0;
        }

        return cost;

    }

    private void applyFilter(List<SegmentEntry> entries) {
        Collection<SegmentEntry> toRemove = new HashSet<SegmentEntry>();
        for (int i = 0; i < entries.size(); i++) {
            SegmentEntry entry = entries.get(i);
            if (!filter.include(entry, i)) {
                toRemove.add(entry);
            }
        }
        entries.removeAll(toRemove);
    }
}
