package com.markatta.stackdetective.clustering;

/**
 *
 * @author johan
 */
public final class Distance<T> implements Comparable<Distance> {

    private final T from;

    private final T to;

    private final int distance;

    public Distance(T from, T to, int distance) {
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    /**
     * Makes the distances default sort on the distance value
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Distance o) {
        
        // TODO this is invalid and causes not-the-same-instances to be treated as the same 
        
        int distanceDiff = distance - o.getDistance();
        if (distanceDiff != 0)
            return distanceDiff;
        
        // avoid returning equal for non-equal objects
        return from.hashCode() - to.hashCode();
    }

    @Override
    public String toString() {
        return "[" + from + "]-[" + to + "]: " + distance;
    }
}
