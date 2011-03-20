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
 * Each method returns a cost of the operation given the index and list of entries.
 * A higher cost means a longer distance because of the operation. The cost may be negative. 
 * 
 * @author johan
 */
public interface DistanceCostStrategy {

    /**
     * Is given the fully qualified exception name of both exceptions
     * @return The cost of the distance between the two exceptions
     */
    int exceptionDistance(String exceptionA, String exceptionB);
    
    /**
     * @return The cost to delete the entry on <code>index</code> in  <code>entries</code>
     */
    int delete(List<Entry> entries, int index);

    /**
     * @return  The cost to add an entry on <code>index</code> in <code>entries</code>
     */
    int add(List<Entry> entries, int index);

    /**
     * @return The cost of replacing the entry on <code>indexA</code> in <code>entriesA</code>
     *         with the entry on <code>indexB</code> from <code>entriesB</code>
     */
    int substitute(List<Entry> entriesA, int indexA, List<Entry> entriesB, int indexB);
    
}
