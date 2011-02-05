
package com.markatta.stackdetective.render.links;

import com.markatta.stackdetective.model.Entry;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class ResolverStackTest {

    public ResolverStackTest() {
    }

    @Test
    public void noUrlFoundReturnsNull() {
        ResolverStack instance = new ResolverStack(new ClassLinkResolver[]{});
        assertNull(instance.getURLFor(new Entry("method", "a.b.Class", "Class.java", 1)));        
    }

}