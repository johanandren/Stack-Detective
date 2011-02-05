
package com.markatta.stackdetective.filter;

import com.markatta.stackdetective.model.Entry;

/**
 *
 * @author johan
 */
public interface EntryFilter {
    public boolean include(Entry entry, int positionInSegment);
}
