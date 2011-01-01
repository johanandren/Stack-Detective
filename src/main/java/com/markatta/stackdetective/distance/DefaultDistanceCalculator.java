package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.SegmentEntry;
import com.markatta.stackdetective.StackTrace;
import com.markatta.stackdetective.TraceSegment;
import com.markatta.stackdetective.distance.cost.DistanceCostStrategy;
import com.markatta.stackdetective.filter.EntryFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses a weighted variation of the levenshtein distance calculation algorithm
 * that will favour similiarities in the beginning of the stack traces.
 * 
 * @author johan
 */
public class DefaultDistanceCalculator implements DistanceCalculator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDistanceCalculator.class);
    
    private final DistanceCostStrategy costStrategy;

    private EntryFilter filter = null;

    public DefaultDistanceCalculator(DistanceCostStrategy costStrategy) {
        this.costStrategy = costStrategy;
    }

    public void setFilter(EntryFilter filter) {
        this.filter = filter;
    }

    public int calculateDistance(StackTrace a, StackTrace b) {
        List<TraceSegment> segmentsForA = a.getSegments();
        List<TraceSegment> segmentsForB = b.getSegments();

        int distance = 0;
        
        // if the root cause is the same exception, it is more alike regardless
        // of stacktrace elements
        if (a.getRootExceptionType() != null && a.getRootExceptionType().equals(b.getRootExceptionType())) {
            distance -= 1000;
            logger.debug("Same root exception type -1000 distance");
        }
        
        
        
        // penalize each segment count difference with 1000
        if (segmentsForA.size() != segmentsForB.size()) {

            int differenceCost = 1000 * Math.abs(segmentsForA.size() - segmentsForB.size());
            logger.debug("Different number of segments, penalizing with " + differenceCost);
            distance += differenceCost;
        }

        // compare each segment with the corresponding one
        int minNumberOfSegments = Math.min(segmentsForA.size(), segmentsForB.size());
        for (int segmentIndex = 0; segmentIndex < minNumberOfSegments; segmentIndex++) {
            TraceSegment segmentA = segmentsForA.get(segmentIndex);
            TraceSegment segmentB = segmentsForB.get(segmentIndex);
            
            int segmentDistance = calculateDistance(segmentA, segmentB);
            
            // dont let each segment cost more than this, this is important
            // as we have heuristic above that depends on these values not 
            // beeing extremely much larger
            segmentDistance = Math.min(25000, segmentDistance);
            
            // also, the last segment is the most important as it always
            // contains the cause, try to favour it a bit
            if (segmentIndex == minNumberOfSegments - 1) {
                segmentDistance *= 2;
            }
            
            logger.debug("Segment " + segmentIndex + " distance " + segmentDistance);
            distance += segmentDistance;
        }

        logger.debug("Total distance " + distance);
        return distance;
    }

    private int calculateDistance(TraceSegment a, TraceSegment b) {
        List<SegmentEntry> entriesForA = a.getEntries();
        List<SegmentEntry> entriesForB = b.getEntries();

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
