package com.markatta.stackdetective.distance.cost;

import com.markatta.stackdetective.model.Entry;
import java.util.List;

/**
 * Just handles location, compares stack elements with equals
 * 
 * @author johan
 */
public final class FavourBeginStrategy implements DistanceCostStrategy {

    public int delete(List<Entry> a, int index) {
        return 1 + index;
    }

    public int add(List<Entry> a, int index) {
        return 1 + index;
    }

    public int substitute(List<Entry> a, int indexA, List<Entry> b, int indexB) {
        Entry entryA = a.get(indexA - 1);
        Entry entryB = b.get(indexB - 1);

        if (entryA.equals(entryB)) {
            return 0;
        } else {
            return 1 + ((indexA + indexB) / 2) ^ 2;
        }


    }
}
