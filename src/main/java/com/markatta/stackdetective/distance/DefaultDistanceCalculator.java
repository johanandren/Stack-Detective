/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.model.Segment;
import com.markatta.stackdetective.distance.cost.DistanceCostStrategy;
import com.markatta.stackdetective.filter.EntryFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Uses a weighted variation of the levenshtein distance calculation algorithm
 * that will favour similiarities in the beginning of the stack traces.
 * 
 * @author johan
 */
public class DefaultDistanceCalculator implements DistanceCalculator<StackTrace> {

    private static final Logger LOGGER = Logger.getLogger(DefaultDistanceCalculator.class);

    private static final int SAME_ROOT_MODIFIER = -100;

    private final DistanceCostStrategy costStrategy;

    private EntryFilter filter = null;

    /**
     * @param costStrategy A way to calculate cost of the differences between
     *                     two {@link Entry} instances.
     */
    public DefaultDistanceCalculator(DistanceCostStrategy costStrategy) {
        this.costStrategy = costStrategy;
    }

    /**
     * @param filter A filter to ignore certain entries when calculating the
     *               distance between the two traces.
     */
    public void setFilter(EntryFilter filter) {
        this.filter = filter;
    }

    @Override
    public int calculateDistance(StackTrace a, StackTrace b) {
        List<Segment> segmentsForA = a.getSegments();
        List<Segment> segmentsForB = b.getSegments();

        int distance = 0;

        // if the root cause is the same exception, it is more alike regardless
        // of stacktrace elements
        if (a.getRootExceptionType() != null && a.getRootExceptionType().equals(b.getRootExceptionType())) {
            distance += SAME_ROOT_MODIFIER;
            LOGGER.debug("Same root exception type, " + SAME_ROOT_MODIFIER + " distance");
        }

        // penalize each segment count difference with 100
        if (segmentsForA.size() != segmentsForB.size()) {

            int differenceCost = 100 * Math.abs(segmentsForA.size() - segmentsForB.size());
            LOGGER.debug("Different number of segments, penalizing with " + differenceCost);
            distance += differenceCost;
        }

        // compare each segment with the corresponding one
        int minNumberOfSegments = Math.min(segmentsForA.size(), segmentsForB.size());
        for (int segmentIndex = 0; segmentIndex < minNumberOfSegments; segmentIndex++) {
            Segment segmentA = segmentsForA.get(segmentIndex);
            Segment segmentB = segmentsForB.get(segmentIndex);

            int segmentDistance = calculateDistance(segmentA, segmentB);

            // dont let each segment cost more than this, this is important
            // as we have heuristic above that depends on these values not 
            // beeing much larger
            segmentDistance = Math.min(25000, segmentDistance);

            // also, the last segment is the most important as it always
            // contains the cause, try to favour it a bit
            if (segmentIndex == minNumberOfSegments - 1) {
                segmentDistance *= 2;
            }

            LOGGER.debug("Segment " + segmentIndex + " distance " + segmentDistance);
            distance += segmentDistance;
        }

        LOGGER.debug("Total distance " + distance);
        // never return sub-zero values
        return Math.max(0, distance);
    }

    private int calculateDistance(Segment a, Segment b) {
        List<Entry> entriesForA = a.getEntries();
        List<Entry> entriesForB = b.getEntries();

        if (filter != null) {
            applyFilter(entriesForA);
            applyFilter(entriesForB);
        }

        int aSize = entriesForA.size();
        int bSize = entriesForB.size();
        int[][] distance = new int[aSize + 1][bSize + 1];
        distance[0][0] = 0;
        for (int bIndex = 1; bIndex <= bSize; bIndex++) {
            // fill [0][*] with the cost for adding each entry in B
            distance[0][bIndex] = costStrategy.add(entriesForB, bIndex);
        }

        for (int i = 1; i <= aSize; i++) {
            // fill [*][0] with the cost for adding each entry in a
            distance[i][0] = distance[i - 1][0] + costStrategy.add(entriesForA, i);
        }

        // i = 1 and j = 1 already filled above
        for (int i = 1; i <= aSize; i++) {
            for (int j = 1; j <= bSize; j++) {
                int deletion = distance[i - 1][j] + costStrategy.delete(entriesForA, i);
                int insertion = distance[i][j - 1] + costStrategy.add(entriesForB, j);
                int substitution = distance[i - 1][j - 1] + costStrategy.substitute(entriesForA, i, entriesForB, j);
                int min = Math.min(deletion, insertion);
                min = Math.min(min, substitution);

                if (LOGGER.isTraceEnabled()) {
                    if (deletion == min) {
                        LOGGER.trace("Chose DELETE, cost: " + min);
                    } else if (insertion == min) {
                        LOGGER.trace("Chose INSERT, cost: " + min);
                    } else {
                        LOGGER.trace("Chose SUBSTITUTE, cost: " + min);
                    }

                }
                distance[i][j] = min;
            }
        }

        if (LOGGER.isTraceEnabled()) {
            // print entire array
            LOGGER.trace("Distance array:");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i <= aSize; i++) {
                for (int j = 0; j <= bSize; j++) {
                    builder.append(distance[i][j]);
                    builder.append("\t");
                }
                builder.append("\n");
            }
            LOGGER.trace(builder.toString());
        }

        return distance[aSize][bSize];
    }

    private void applyFilter(List<Entry> entries) {
        Collection<Entry> toRemove = new HashSet<Entry>();
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (!filter.include(entry, i)) {
                toRemove.add(entry);
            }
        }
        entries.removeAll(toRemove);
    }
}
