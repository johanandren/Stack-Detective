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
package com.markatta.stackdetective.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Primary model object for stacktraces. Contains one or more TraceSegment
 * representing the different parts of the stacktrace.
 *
 * @author johan
 */
public final class StackTrace {

    private final List<Segment> segments;

    public StackTrace(List<Segment> segments) {
        this.segments = segments;
    }

    public StackTrace() {
        segments = new ArrayList<Segment>();
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    public int numberOfSegments() {
        return segments.size();
    }

    /**
     * An unmodifiable List of the segments
     */
    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public Segment getCauseSegment() {
        return segments.get(segments.size() - 1);
    }

    public String getRootExceptionType() {
        return getCauseSegment().getExceptionType();
    }

    /**
     * @return All SegmentEntries from all segments of this stacktrace
     */
    public List<Entry> flatten() {
        List<Entry> entries = new ArrayList<Entry>();
        for (Segment traceSegment : segments) {
            entries.addAll(traceSegment.getEntries());
        }
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Segment traceSegment : segments) {
            builder.append(traceSegment.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
