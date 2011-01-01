package com.markatta.stackdetective.distance.cost;

import com.markatta.stackdetective.SegmentEntry;
import java.util.List;

/**
 * Each method returns a cost of the operation given the index and list of entries.
 * A higher cost means a longer distance because of the operation. The cost may be negative. 
 * 
 * @author johan
 */
public interface DistanceCostStrategy {

    int delete(List<SegmentEntry> a, int index);

    int add(List<SegmentEntry> a, int index);

    int substitute(List<SegmentEntry> a, int indexA, List<SegmentEntry> b, int indexB);
}
