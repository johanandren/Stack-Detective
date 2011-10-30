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
package com.markatta.stackdetective.distance.levehnstein;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.markatta.stackdetective.distance.DistanceAlgorithm;
import com.markatta.stackdetective.filter.EntryFilter;
import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.Segment;
import com.markatta.stackdetective.model.StackTrace;

/**
 * Uses a weighted variation of the levenshtein distance calculation algorithm
 * that will favour similiarities in the beginning of the stack traces.
 * 
 * @author johan
 */
public final class WeightedLevehnsteinDistance implements DistanceAlgorithm<StackTrace> {

	private static final Logger LOGGER = Logger.getLogger(WeightedLevehnsteinDistance.class.getName());

	private final DistanceCostStrategy costStrategy;

	private EntryFilter filter = null;

	public WeightedLevehnsteinDistance() {
		this.costStrategy = new IntelligentSubstitutionStrategy();
	}

	/**
	 * @param costStrategy
	 *            A way to calculate cost of the differences between two
	 *            {@link Entry} instances.
	 */
	WeightedLevehnsteinDistance(DistanceCostStrategy costStrategy) {
		this.costStrategy = costStrategy;
	}

	/**
	 * @param filter
	 *            A filter to ignore certain entries when calculating the
	 *            distance between the two traces.
	 */
	public void setFilter(EntryFilter filter) {
		this.filter = filter;
	}

	@Override
	public double calculateDistance(StackTrace a, StackTrace b) {
		double distance = 0.0;

		Iterator<Segment> iteratorA = a.getSegments().iterator();
		Iterator<Segment> iteratorB = b.getSegments().iterator();
		while (iteratorA.hasNext() && iteratorB.hasNext()) {
			Segment segmentA = iteratorA.next();
			Segment segmentB = iteratorB.next();

			distance += calculateDistanceFor(segmentA, segmentB);
		}

		LOGGER.log(Level.FINE, "Calculated distance {0}", distance);

		// limit result to 0 =< result <= 1
		final double normalized = normalize(distance);
		LOGGER.log(Level.FINE, "Normalized distance {0}", normalized);

		// and invert as the interface says alike == 1, not alike == 0
		final double result = 1.0 - normalized;
		LOGGER.log(Level.FINE, "Distance {0}", result);

		return result;
	}

	private double normalize(double distance) {
		if (distance >= 1) {
			return 1;
		} else if (distance <= 0) {
			return 0;
		} else {
			return distance;
		}
	}

	private double calculateDistanceFor(Segment a, Segment b) {
		LevehnsteinDistanceCalculation calculationData = performCalculation(a, b);
		return calculationData.getResult();
	}

	private LevehnsteinDistanceCalculation performCalculation(Segment a, Segment b) {
		List<Entry> entriesForA = a.getEntries();
		List<Entry> entriesForB = b.getEntries();

		if (filter != null) {
			applyFilter(entriesForA);
			applyFilter(entriesForB);
		}
		LevehnsteinDistanceCalculation calculationData = new LevehnsteinDistanceCalculation(entriesForA, entriesForB,
				costStrategy);
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
