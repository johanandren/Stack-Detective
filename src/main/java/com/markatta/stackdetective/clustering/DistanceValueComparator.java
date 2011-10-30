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

import java.util.Comparator;

/**
 * Comparator to sort distances on the distance value. NOTE: not consistent with equals as recommended in Comparator javadoc.
 * 
 * @author johan
 */
final class DistanceValueComparator implements Comparator<Distance> {

    @Override
    public int compare(Distance o1, Distance o2) {
        double distanceDiff = o1.getDistance() - o2.getDistance();
        if (distanceDiff != 0) {
        	if (distanceDiff> 0) {
        		return 1;
        	} else { 
        		return -1;
        	}
        }

        // avoid returning equal for non-equal objects
        return o1.hashCode() - o2.hashCode();
    }
}
