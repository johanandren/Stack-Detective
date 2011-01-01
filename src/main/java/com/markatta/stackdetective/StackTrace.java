package com.markatta.stackdetective;

import java.util.ArrayList;
import java.util.List;

/**
 * Primary model object for stacktraces. Contains one or more TraceSegment
 * representing the different parts of the stacktrace.
 *
 * @author johan
 */
public final class StackTrace {

    private final List<TraceSegment> segments;

    public StackTrace(List<TraceSegment> segments) {
        this.segments = segments;
    }

    public int numberOfSegments() {
        return segments.size();
    }

    public List<TraceSegment> getSegments() {
        return segments;
    }

    /**
     * @return All SegmentEntries from all segments of this stacktrace
     */
    public List<SegmentEntry> flatten() {
        List<SegmentEntry> entries = new ArrayList<SegmentEntry>();
        for (TraceSegment traceSegment : segments) {
            entries.addAll(traceSegment.getEntries());
        }
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (TraceSegment traceSegment : segments) {
            builder.append(traceSegment.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
