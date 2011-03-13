/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markatta.stackdetective.render;

import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.model.Segment;
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
        List<Entry> ignoredEntries = new ArrayList<Entry>();

        for (int segmentIndex = 0; segmentIndex < stackTrace.getSegments().size(); segmentIndex++) {
            Segment segment = stackTrace.getSegments().get(segmentIndex);
            
            renderPreSegment(builder, stackTrace.getSegments(), segmentIndex);

            for (int entryIndex = 0; entryIndex < segment.numberOfEntries(); entryIndex++) {
                Entry entry = segment.getEntries().get(entryIndex);

                if (filter == null) {
                    renderEntry(builder, segment.getEntries(), entryIndex);
                } else if (filter.include(entry, entryIndex)) {
                    // before this there were ignored entries
                    if (!ignoredEntries.isEmpty()) {
                        renderIgnoredEntries(builder, ignoredEntries, entryIndex - ignoredEntries.size());
                        ignoredEntries.clear();
                    }
                    renderEntry(builder, segment.getEntries(), entryIndex);
                } else {
                    ignoredEntries.add(entry);
                }
            }
            // the stack trace ends with ignored entries
            if (!ignoredEntries.isEmpty()) {
                renderIgnoredEntries(builder, ignoredEntries, segment.numberOfEntries() - ignoredEntries.size());
                ignoredEntries.clear();
            }

            renderPostSegment(builder, stackTrace.getSegments(), segmentIndex);
            
            segmentIndex++;
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
     * 
     * @param builder Add text to this builder
     * @param segment The stack trace segment
     * @param segmentIndex The number of the segment in the trace, starting with 0
     */
    protected void renderPreSegment(StringBuilder builder, List<Segment> segments, int segmentIndex) {
    }

    /**
     * Add text after iterating over the entries of the stack trace
     * @param builder Add text to this builder
     * @param segment The stack trace segment
     * @param segmentIndex The number of the segment in the trace, starting with 0
     */
    protected void renderPostSegment(StringBuilder builder, List<Segment> segments, int segmentIndex) {
    }

    /**
     * Add text for one entry. Will not be called if there is a filter that says
     * that the entry should be ignored.
     * 
     * @param builder Add text to this builder
     * @param trace The stack trace
     * @param entryIndex  The number of the entry in the segment, starting with 0
     */
    protected void renderEntry(StringBuilder builder, List<Entry> entries, int entryIndex) {
    }

    /**
     * Add text for one or more entries that has been ignored.
     * @param builder
     * @param entry 
     * @param firstEntryIndex the index in the segment of the first entry that was ignored starting with 1
     */
    protected void renderIgnoredEntries(StringBuilder builder, List<Entry> ignoredEntries, int firstEntryIndex) {
    }
}
