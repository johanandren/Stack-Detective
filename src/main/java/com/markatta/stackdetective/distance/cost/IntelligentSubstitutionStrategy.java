package com.markatta.stackdetective.distance.cost;

import com.markatta.stackdetective.SegmentEntry;
import java.util.List;

/**
 * A weighted cost calculator that makes differences in the beginning and end of the stack
 * trace to cost much more than differences mid-trace. Substitution calculation is done
 * with a stack trace specific algorithm.
 *
 * @author johan
 */
public final class IntelligentSubstitutionStrategy implements DistanceCostStrategy {

    public int delete(List<SegmentEntry> a, int index) {
        int halfSize = a.size() / 2;
        return 1000 * 2 * (1 + halfSize - Math.abs(index - halfSize));
    }

    public int add(List<SegmentEntry> a, int index) {
        // identic
        return delete(a, index);
    }

    public int substitute(List<SegmentEntry> a, int indexA, List<SegmentEntry> b, int indexB) {
        SegmentEntry entryA = a.get(indexA - 1);
        SegmentEntry entryB = b.get(indexB - 1);

        int entryDifferenceCost = compareFrames(entryA, entryB);
        int halfSizeA = a.size() / 2;
        int halfSizeB = b.size() / 2;

        return (halfSizeA * Math.abs(indexA - halfSizeA) + halfSizeB - Math.abs(indexB - halfSizeB)) * entryDifferenceCost;

    }

    private int compareFrames(SegmentEntry entryA, SegmentEntry entryB) {

        if (entryA.equals(entryB)) {
            // exact same entry, make sure it really stands out
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
