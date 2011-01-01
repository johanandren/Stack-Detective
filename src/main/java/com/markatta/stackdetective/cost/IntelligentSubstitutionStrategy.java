package com.markatta.stackdetective.cost;

import com.markatta.stackdetective.SegmentEntry;
import java.util.List;

/**
 * Contains a more intelligent comparison of entries for substitution.
 *
 * @author johan
 */
public class IntelligentSubstitutionStrategy implements DistanceCostStrategy {

    public int delete(List<SegmentEntry> a, int index) {
        return 1000 * (1 + index);
    }

    public int add(List<SegmentEntry> a, int index) {
        return 1000 * (1 + index);
    }

    public int substitute(List<SegmentEntry> a, int indexA, List<SegmentEntry> b, int indexB) {
        SegmentEntry entryA = a.get(indexA - 1);
        SegmentEntry entryB = b.get(indexB - 1);

        int cost = compareFrames(entryA, entryB);

        return Math.max(0, 1 + ((indexA + indexB) / 2) * cost);

    }

    private int compareFrames(SegmentEntry entryA, SegmentEntry entryB) {
        if (entryA.equals(entryB)) {
            return -1;
        }

        if (!entryA.getClassName().equals(entryB.getClassName())) {
            return 1000;
        } else if (!entryA.getMethodName().equals(entryB.getMethodName())) {
            return 100;
        } else {
            return 1;
        }
    }
}
