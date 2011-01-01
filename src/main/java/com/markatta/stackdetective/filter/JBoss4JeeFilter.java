package com.markatta.stackdetective.filter;

import com.markatta.stackdetective.SegmentEntry;

/**
 *
 * @author johan
 */
public class JBoss4JeeFilter implements EntryFilter {

    private static final String[] ignored = {
        "java.lang.reflect",
        "sun.reflect",
        "org.jboss.aspects.tx",
        "org.jboss.ejb3",
        "org.jboss.aop",
        "$Proxy"
    };

    public boolean include(SegmentEntry entry, int positionInSegment) {
        
        // always include the first line
        if (positionInSegment == 0) {
            return true;
        }
        
        for (String string : ignored) {
            if (entry.getClassName().startsWith(string)) {
                return false;
            }
        }


        return true;
    }
}
