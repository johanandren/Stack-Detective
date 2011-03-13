package com.markatta.stackdetective.clustering;

import com.markatta.stackdetective.distance.DistanceCalculator;
import org.junit.Test;
import static org.junit.Assert.*;

public class DistanceMatrixTest {

    @Test
    public void afterAddingTwoEntriesEveryPossibleCombinationHasADistance() {
        DistanceMatrix<String> instance = new DistanceMatrix<String>(new DistanceCalculator<String>() {

            private int distance = 1;

            @Override
            public int calculateDistance(String a, String b) {
                return distance;
            }
        });

        String a = "A";
        String b = "B";

        instance.add(a);
        instance.add(b);
        
        assertEquals(0, instance.getDistanceBetween(a, a));
        assertEquals(0, instance.getDistanceBetween(b, b));
        assertEquals(1, instance.getDistanceBetween(a, b));
        assertEquals(1, instance.getDistanceBetween(b, a));
    }
}