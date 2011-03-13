package com.markatta.stackdetective.clustering;

import java.util.Comparator;

public class DistanceValueComparator implements Comparator<Distance> {

    @Override
    public int compare(Distance o1, Distance o2) {
        // TODO this is invalid and causes not-the-same-instances to be treated as the same 

        int distanceDiff = o1.getDistance() - o2.getDistance();
        if (distanceDiff != 0) {
            return distanceDiff;
        }

        // avoid returning equal for non-equal objects
        return o1.hashCode() - o2.hashCode();
    }
}
