package com.markatta.stackdetective.render;

import com.markatta.stackdetective.SegmentEntry;
import com.markatta.stackdetective.TraceSegment;

/**
 * Renders stacktraces identical to the toString on Exception (possibly filtering
 * lines that are not interesting)
 * 
 * @author johan
 */
public class SimpleTextRenderer extends AbstractStackTraceRenderer {

    private static final String LINE_PAD = "  ";

    @Override
    protected void renderPreSegment(StringBuilder builder, TraceSegment segment) {
        builder.append(segment.getExceptionText());
        builder.append("\n");
    }

    @Override
    protected void renderEntry(StringBuilder builder, SegmentEntry entry) {
        builder.append(LINE_PAD);
        builder.append("at ");
        builder.append(entry.getClassName());
        builder.append(entry.getMethodName());
        builder.append("(");
        builder.append(entry.getFileName());
        builder.append(":");
        builder.append(entry.getLineNumber());
        builder.append(")\n");
    }
}
