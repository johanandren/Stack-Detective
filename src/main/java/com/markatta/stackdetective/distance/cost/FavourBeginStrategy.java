package com.markatta.stackdetective.distance.cost;

import com.markatta.stackdetective.SegmentEntry;
import java.util.List;

/**
 * Just handles location, compares stack elements with equals
 * 
 * @author johan
 */
public final class FavourBeginStrategy implements DistanceCostStrategy {

    public int delete(List<SegmentEntry> a, int index) {
        return 1 + index;
    }

    public int add(List<SegmentEntry> a, int index) {
        return 1 + index;
    }

    public int substitute(List<SegmentEntry> a, int indexA, List<SegmentEntry> b, int indexB) {
        SegmentEntry entryA = a.get(indexA - 1);
        SegmentEntry entryB = b.get(indexB - 1);

        if (entryA.equals(entryB)) {
            return 0;
        } else {
            return 1 + ((indexA + indexB) / 2) ^ 2;
        }


    }
}
