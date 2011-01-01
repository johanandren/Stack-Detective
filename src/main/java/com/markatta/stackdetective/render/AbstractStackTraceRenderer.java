package com.markatta.stackdetective.render;

import com.markatta.stackdetective.SegmentEntry;
import com.markatta.stackdetective.StackTrace;
import com.markatta.stackdetective.TraceSegment;
import com.markatta.stackdetective.filter.EntryFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains hooks for rendering the various parts of the stack trace. 
 *
 * @author johan
 */
public abstract class AbstractStackTraceRenderer implements StackTraceRenderer {

    private EntryFilter filter;

    public void setFilter(EntryFilter filter) {
        this.filter = filter;
    }

    public final String render(StackTrace stackTrace) {
        StringBuilder builder = new StringBuilder();
        renderPreTrace(builder, stackTrace);
        List<SegmentEntry> ignoredEntries = new ArrayList<SegmentEntry>();

        for (TraceSegment segment : stackTrace.getSegments()) {
            renderPreSegment(builder, segment);

            for (int entryNumber = 0; entryNumber < segment.numberOfEntries(); entryNumber++) {
                SegmentEntry entry = segment.getEntries().get(entryNumber);

                if (filter == null) {
                    renderEntry(builder, entry);
                } else if (filter.include(entry, entryNumber)) {
                    // before this there were ignored entries
                    if (!ignoredEntries.isEmpty()) {
                        renderIgnoredEntries(builder, ignoredEntries);
                        ignoredEntries.clear();
                    }
                    renderEntry(builder, entry);
                } else {
                    ignoredEntries.add(entry);
                }
            }
            // the stack trace ends with ignored entries
            if (!ignoredEntries.isEmpty()) {
                renderIgnoredEntries(builder, ignoredEntries);
                ignoredEntries.clear();
            }

            renderPostSegment(builder, segment);
        }

        renderPostTrace(builder, stackTrace);
        return builder.toString();
    }

    /**
     * Add text before iterating over the segments of the stack trace
     * @param builder Add text to this builder
     * @param trace The stack trace
     */
    protected void renderPreTrace(StringBuilder builder, StackTrace trace) {
    }

    /**
     * Add text after iterating over all the segments of the stack trace
     * @param builder Add text to this builder
     * @param trace The stack trace
     */
    protected void renderPostTrace(StringBuilder builder, StackTrace trace) {
    }

    /**
     * Add text before iterating over the entries of the segment. (For example
     * the text of the exception)
     * @param builder Add text to this builder
     * @param trace The stack trace
     */
    protected void renderPreSegment(StringBuilder builder, TraceSegment segment) {
    }

    /**
     * Add text after iterating over the entries of the stack trace
     * @param builder Add text to this builder
     * @param trace The stack trace
     */
    protected void renderPostSegment(StringBuilder builder, TraceSegment segment) {
    }

    /**
     * Add text for one entry. Will not be called if there is a filter that says
     * that the entry should be ignored.
     * 
     * @param builder Add text to this builder
     * @param trace The stack trace
     */
    protected void renderEntry(StringBuilder builder, SegmentEntry entry) {
    }

    /**
     * Add text for one or more entries that has been ignored.
     * @param builder
     * @param entry 
     */
    protected void renderIgnoredEntries(StringBuilder builder, List<SegmentEntry> ignoredEntries) {
    }
}
