package com.markatta.stackdetective.clustering;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
