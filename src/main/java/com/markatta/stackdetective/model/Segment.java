package com.markatta.stackdetective.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains one throw->catch block
 * 
 * @author johan
 */
public final class Segment {

    private final String exceptionType;

    private final String exceptionText;

    private final List<Entry> entries;

    /**
     * @param exceptionType The exception class
     * @param exceptionText The message from the exception class
     * @param entries The individual entries in the trace
     */
    public Segment(String exceptionType, String exceptionText, List<Entry> entries) {
        this.exceptionType = exceptionType;
        this.exceptionText = exceptionText;
        this.entries = entries;
    }
    
    public Segment(String exceptionType, String exceptionText) {
        this.exceptionType = exceptionType;
        this.exceptionText = exceptionText;
        this.entries = new ArrayList<Entry>();
    }
    
    public void addEntry(Entry entry) {
        entries.add(entry);
    }
    
    /**
     * @return The class name of the exception of this block
     */
    public String getExceptionType() {
        return exceptionType;
    }

    /**
     * @return The message text of the exception for this block
     */
    public String getExceptionText() {
        return exceptionText;
    }

    public List<Entry> getEntries() {
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

        for (Entry segmentEntry : entries) {
            builder.append(segmentEntry.toString());
            builder.append("\n");
        }
        if (!entries.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }


        return builder.toString();
    }
}
