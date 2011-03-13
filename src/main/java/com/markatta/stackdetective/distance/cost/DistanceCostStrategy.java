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

    int delete(List<Entry> a, int index);

    int add(List<Entry> a, int index);

    int substitute(List<Entry> a, int indexA, List<Entry> b, int indexB);
}
