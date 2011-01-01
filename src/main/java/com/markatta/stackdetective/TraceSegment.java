package com.markatta.stackdetective;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains one throw->catch block
 * 
 * @author johan
 */
public final class TraceSegment {

    private final String exceptionText;

    private final List<SegmentEntry> entries;

    public TraceSegment(String exceptionText, List<SegmentEntry> entries) {
        this.exceptionText = exceptionText;
        this.entries = entries;
    }

    public String getExceptionText() {
        return exceptionText;
    }

    public List<SegmentEntry> getEntries() {
        return entries;
    }

    public int numberOfEntries() {
        return entries.size();
    }

   

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (exceptionText != null) {
            builder.append(exceptionText);
            builder.append("\n");
        }

        for (SegmentEntry segmentEntry : entries) {
            builder.append(segmentEntry.toString());
            builder.append("\n");
        }
        if (!entries.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }


        return builder.toString();
    }
}
