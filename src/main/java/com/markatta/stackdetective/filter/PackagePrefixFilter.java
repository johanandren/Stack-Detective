
package com.markatta.stackdetective.filter;

import com.markatta.stackdetective.model.Entry;

/**
 * Ignores all stacktrace lines that starts with any of the given package
 * prefixes except for if the entry occurs in the first line of the stack trace
 * in which case it is always kept.
 * 
 * @author johan
 */
public class PackagePrefixFilter implements EntryFilter {

    private final String[] packagePrefixes;

    public PackagePrefixFilter(String[] packagePrefixes) {
        this.packagePrefixes = packagePrefixes;
    }
    
    public boolean include(Entry entry, int positionInSegment) {
        
        // always include the first line
        if (positionInSegment == 0) {
            return true;
        }
        
        for (String string : packagePrefixes) {
            if (entry.getPackageName().startsWith(string)) {
                return false;
            }
        }


        return true;
    }

    
}
