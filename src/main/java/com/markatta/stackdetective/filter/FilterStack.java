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
 * Combines multiple filters into one. If any filter says a line should be
 * filtered it will be filtered.
 * 
 * @author johan
 */
public final class FilterStack implements EntryFilter {

    private final EntryFilter[] filters;

    public FilterStack(EntryFilter ... filters) {
        this.filters = filters;
    }

    @Override
    public boolean include(Entry entry, int positionInSegment) {
        for (int i = 0; i < filters.length; i++) {
            EntryFilter entryFilter = filters[i];
            if (!entryFilter.include(entry, positionInSegment)) {
                return false;
            }
        }
        return true;
    }
}
