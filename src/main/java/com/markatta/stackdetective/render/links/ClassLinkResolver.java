
package com.markatta.stackdetective.render.links;

import com.markatta.stackdetective.model.Entry;

/**
 *
 * @author johan
 */
public interface ClassLinkResolver {
    
    /**
     * @return An URL to the class for the given entry or <code>null</code> if no
     *         URL could be resolved.
     */
    public String getURLFor(Entry entry);
}
