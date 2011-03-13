
package com.markatta.stackdetective.distance;

/**
 *
 * @author johan
 */
public interface DistanceCalculator<T> {
    /**
     * Calculate distance between <code>a</code> and <code>b</code>
     * @return a positive or zero value where zero means that the two objects
     *         very much alike eachother and a high value means that they have
     *         nothing in common.
     */
    public int calculateDistance(T a, T b);
}
