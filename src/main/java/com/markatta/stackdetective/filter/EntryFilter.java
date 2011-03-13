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
package com.markatta.stackdetective.filter;

import com.markatta.stackdetective.model.Entry;

/**
 * Determine if a given stack trace {@link Entry} should be filtered out or included.
 * 
 * @author johan
 */
public interface EntryFilter {

    /**
     * @param entry The entry
     * @param positionInSegment The position (line) in the segment at which the entry occured
     * @return <code>true</code> if the entry should be included, <code>false</code> if it should be ignored.
     */
    public boolean include(Entry entry, int positionInSegment);
}
