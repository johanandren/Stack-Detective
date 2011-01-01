package com.markatta.stackdetective.cost;

import com.markatta.stackdetective.SegmentEntry;
import java.util.List;

/**
 *
 * @author johan
 */
public interface DistanceCostStrategy {

    int delete(List<SegmentEntry> a, int index);

    int add(List<SegmentEntry> a, int index);

    int substitute(List<SegmentEntry> a, int indexA, List<SegmentEntry> b, int indexB);
}
