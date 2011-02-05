
package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.model.StackTrace;

/**
 *
 * @author johan
 */
public interface DistanceCalculator {
    /**
     * Calculate distance between stack trace <code>a</code> and trace <code>b</code>
     * @return a positive or zero value where zero means that the two traces
     *         very much alike eachother and a high value means that they have
     *         nothing in common.
     */
    public int calculateDistance(StackTrace a, StackTrace b);
}
