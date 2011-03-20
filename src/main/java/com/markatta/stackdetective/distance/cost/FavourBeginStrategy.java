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
package com.markatta.stackdetective.distance.cost;

import com.markatta.stackdetective.model.Entry;
import java.util.List;

/**
 * Just handles location, compares stack elements with equals. Differences
 * in the end are more expensive than differences in the beginning of the
 * stack traces.
 * 
 * @author johan
 */
public final class FavourBeginStrategy implements DistanceCostStrategy {

    @Override
    public int delete(List<Entry> a, int index) {
        return 1 + index;
    }

    @Override
    public int add(List<Entry> a, int index) {
        return 1 + index;
    }

    @Override
    public int substitute(List<Entry> a, int indexA, List<Entry> b, int indexB) {
        Entry entryA = a.get(indexA - 1);
        Entry entryB = b.get(indexB - 1);

        if (entryA.equals(entryB)) {
            return 0;
        } else {
            return 1 + ((indexA + indexB) / 2);
        }


    }

    @Override
    public int exceptionDistance(String exceptionA, String exceptionB) {
        if (exceptionA.equals(exceptionB)) {
            return 0;
        } else {
            return 200;
        }
    }
}
