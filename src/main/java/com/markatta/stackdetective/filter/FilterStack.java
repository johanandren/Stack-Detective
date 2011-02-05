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
