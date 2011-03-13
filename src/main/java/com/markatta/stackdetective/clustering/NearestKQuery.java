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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * Implementation of the KNN-algorithm. 
 * Finds the k nearest neighbours to an item.
 * 
 * @author johan
 */
public class NearestKQuery<T> {

    private final DistanceMatrix<T> distanceMatrix;

    public NearestKQuery(DistanceMatrix<T> distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    /**
     * Returns a map sorted on distance from <code>item</code> with the
     * k nearest items to <code>item</code> from
     * the distance matrix the query was created with.
     */
    public List<Distance<T>> getNearestK(T item, int k) {
        SortedSet<Distance<T>> distances = distanceMatrix.getDistancesFrom(item);
        List<Distance<T>> result = new ArrayList<Distance<T>>(k);
        
        for (Distance<T> distance : distances) {
            if (distance.getTo() == item) {
                continue;
            }
            if (result.size() == k) {
                break;
            }

            result.add(distance);
            
        }

        return result;
    }
}
