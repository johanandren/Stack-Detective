package com.markatta.stackdetective.distance.cost;

import com.markatta.stackdetective.model.Entry;
import java.util.List;

/**
 * Each method returns a cost of the operation given the index and list of entries.
 * A higher cost means a longer distance because of the operation. The cost may be negative. 
 * 
 * @author johan
 */
public interface DistanceCostStrategy {

    int delete(List<Entry> a, int index);

    int add(List<Entry> a, int index);

    int substitute(List<Entry> a, int indexA, List<Entry> b, int indexB);
}
