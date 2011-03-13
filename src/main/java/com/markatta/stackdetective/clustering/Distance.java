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

/**
 * Generic model for distance between two objects.
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
