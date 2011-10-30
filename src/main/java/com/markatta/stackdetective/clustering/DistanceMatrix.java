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
package com.markatta.stackdetective.clustering;

import com.markatta.stackdetective.distance.DistanceCalculator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Dynamic distance matrix, that grows as elements are added. Keeps
 * the respektive distance maps sorted on distance at all times.
 * 
 * @author johan
 */
public class DistanceMatrix<T> {

    private final Map<T, SortedSet<Distance<T>>> distances = new HashMap<T, SortedSet<Distance<T>>>();

    private final DistanceCalculator<T> calculator;

    /**
     * @param calculator A calculator used for calculating the actual distances.
     */
    public DistanceMatrix(DistanceCalculator<T> calculator) {
        this.calculator = calculator;
    }

    public void add(T newItem) {
        Set<Distance<T>> distancesFromThis = getOrCreateDistanceSetFrom(newItem);

        for (T otherItem : distances.keySet()) {
            addDistanceBetween(newItem, otherItem, distancesFromThis);
        }

    }

    private void addDistanceBetween(T newItem, T otherItem, Set<Distance<T>> distancesForThis) {
        if (otherItem == newItem) {
            // small optimization, never calculate distance to itself
            distancesForThis.add(new Distance<T>(newItem, newItem, 0));

        } else {
            Set<Distance<T>> distancesForOther = getOrCreateDistanceSetFrom(otherItem);

            // from this to the other
            int distance = calculator.calculateDistance(newItem, otherItem);
            distancesForThis.add(new Distance<T>(newItem, otherItem, distance));

            // from other to this
            distance = calculator.calculateDistance(otherItem, newItem);
            distancesForOther.add(new Distance<T>(otherItem, newItem, distance));
        }
    }

    /**
     * Get the map of distances from the given stack trace and every other stacktrace.
     * If the map does not exist previously it is created and added to the distances map.
     */
    private SortedSet<Distance<T>> getOrCreateDistanceSetFrom(T item) {
        if (!distances.containsKey(item)) {
            distances.put(item, new TreeSet<Distance<T>>());
        }

        return distances.get(item);
    }

    public SortedSet<Distance<T>> getDistancesFrom(T item) {
        return distances.get(item);
    }

    public int getDistanceBetween(T a, T b) {
        if (!distances.containsKey(a)) {
            throw new IllegalArgumentException("StackTrace a has not been added to the matrix");
        }
        if (!distances.containsKey(b)) {
            throw new IllegalArgumentException("StackTrace b has not been added to the matrix");
        }

        SortedSet<Distance<T>> distancesFromA = getDistancesFrom(a);
        for (Distance<T> distance : distancesFromA) {
            if (distance.getTo() == b) {
                return distance.getDistance();
            }
        }

        throw new IllegalStateException("Should not be possible, could not find the distance between a and b!?!");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + distances + "]";
    }
}
