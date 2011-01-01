
package com.markatta.stackdetective.filter;

import com.markatta.stackdetective.SegmentEntry;

/**
 *
 * @author johan
 */
public interface EntryFilter {
    public boolean include(SegmentEntry entry, int positionInSegment);
}
