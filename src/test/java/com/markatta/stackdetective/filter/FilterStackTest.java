package com.markatta.stackdetective.filter;

import com.markatta.stackdetective.model.Entry;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class FilterStackTest {

    @Test
    public void testInclude() {
        FilterStack instance = new FilterStack(new EntryFilter[]{
                    new EntryFilter()    {

                        public boolean include(Entry entry, int positionInSegment) {
                            return true;
                        }
                    }
                });
        assertTrue(instance.include(new Entry("method", "Class", "Class.java", 1), 1));
    }

    @Test
    public void testExclude() {
        FilterStack instance = new FilterStack(new EntryFilter[]{
                    new EntryFilter()    {

                        public boolean include(Entry entry, int positionInSegment) {
                            return false;
                        }
                    }
                });
        assertFalse(instance.include(new Entry("method", "Class", "Class.java", 1), 1));
    }

    @Test
    public void excludeAlwaysWins() {
        FilterStack instance = new FilterStack(new EntryFilter[]{
                    new EntryFilter()    {

                        public boolean include(Entry entry, int positionInSegment) {
                            return true;
                        }
                    },
                    new EntryFilter()    {

                        public boolean include(Entry entry, int positionInSegment) {
                            return false;
                        }
                    }
                });
        assertFalse(instance.include(new Entry("method", "Class", "Class.java", 1), 1));

    }
}