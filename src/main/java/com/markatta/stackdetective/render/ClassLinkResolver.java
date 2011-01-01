
package com.markatta.stackdetective.render;

import com.markatta.stackdetective.SegmentEntry;

/**
 *
 * @author johan
 */
public interface ClassLinkResolver {
    
    /**
     * @return An URL to the class for the given entry or <code>null</code> if no
     *         URL could be resolved.
     */
    public String getLinkFor(SegmentEntry entry);
}
