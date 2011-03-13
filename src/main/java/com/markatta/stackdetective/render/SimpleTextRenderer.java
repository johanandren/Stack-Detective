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
import com.markatta.stackdetective.model.Segment;
import java.util.List;

/**
 * Renders stacktraces identical to the toString on Exception (possibly filtering
 * lines that are not interesting)
 * 
 * @author johan
 */
public final class SimpleTextRenderer extends AbstractStackTraceRenderer {

    private static final String LINE_PAD = "  ";

    @Override
    protected void renderPreSegment(StringBuilder builder, List<Segment> segments, int segmentIndex) {
        Segment segment = segments.get(segmentIndex);
        if (segments.size() > 1 && segmentIndex > 0) {
            // for all but first in a multisegment trace
            builder.append("Caused by: ");
        }
        builder.append(segment.getExceptionType());
        builder.append(": ");
        builder.append(segment.getExceptionText());
        builder.append("\n");
    }

    @Override
    protected void renderEntry(StringBuilder builder, List<Entry> entries, int entryIndex) {
        Entry entry = entries.get(entryIndex);
        builder.append(LINE_PAD);
        builder.append("at ");
        builder.append(entry.getFqClassName());
        builder.append(entry.getMethodName());
        builder.append("(");
        builder.append(entry.getFileName());
        builder.append(":");
        builder.append(entry.getLineNumber());
        builder.append(")\n");
    }
}
