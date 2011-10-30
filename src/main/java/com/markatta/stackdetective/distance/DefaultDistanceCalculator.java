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
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Uses a weighted variation of the levenshtein distance calculation algorithm
 * that will favour similiarities in the beginning of the stack traces.
 * 
 * @author johan
 */
public final class DefaultDistanceCalculator implements DistanceCalculator<StackTrace> {

    private static final Logger LOGGER = Logger.getLogger(DefaultDistanceCalculator.class);

    private static final int DIFFERENT_LENGTH_COST_PER_SEGMENT = 500;

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
        int distance = 0;

        Iterator<Segment> iteratorA = a.getSegments().iterator();
        Iterator<Segment> iteratorB = b.getSegments().iterator();
        while (iteratorA.hasNext() && iteratorB.hasNext()) {
            Segment segmentA = iteratorA.next();
            Segment segmentB = iteratorB.next();
            
            distance += costStrategy.exceptionDistance(segmentA.getExceptionType(), segmentB.getExceptionType());

            distance += calculateDistanceFor(segmentA, segmentB);
        }

        // punish different lengths
        distance += calculatSegmentDiffCost(iteratorA);
        distance += calculatSegmentDiffCost(iteratorB);

        LOGGER.debug("Total distance " + distance);

        // never return sub-zero values
        return Math.max(0, distance);
    }

    private int calculatSegmentDiffCost(Iterator<Segment> iterator) {
        int cost = 0;
        while (iterator.hasNext()) {
            iterator.next();
            cost += DIFFERENT_LENGTH_COST_PER_SEGMENT;
        }
        return cost;
    }

    private int calculateDistanceFor(Segment a, Segment b) {
        LevehnsteinDistanceCalculation calculationData = performCalculation(a, b);
        return calculationData.getResult();
    }

    @Override
    public List<BackTrackElement> getDistanceBacktrack(StackTrace a, StackTrace b) {
        LevehnsteinDistanceCalculation calculationData = performCalculation(a.getCauseSegment(), b.getCauseSegment());
        return calculationData.getBackTrack();
    }

    private LevehnsteinDistanceCalculation performCalculation(Segment a, Segment b) {
        List<Entry> entriesForA = a.getEntries();
        List<Entry> entriesForB = b.getEntries();

        if (filter != null) {
            applyFilter(entriesForA);
            applyFilter(entriesForB);
        }
        LevehnsteinDistanceCalculation calculationData =
                new LevehnsteinDistanceCalculation(entriesForA, entriesForB, costStrategy);
        calculationData.calculateDistance();
        calculationData.printEntireArrayToTraceLog();
        calculationData.printBackTrackToTraceLog();
        return calculationData;
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
