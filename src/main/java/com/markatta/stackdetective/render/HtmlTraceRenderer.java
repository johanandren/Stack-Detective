package com.markatta.stackdetective.render;

import com.markatta.stackdetective.SegmentEntry;
import com.markatta.stackdetective.StackTrace;
import com.markatta.stackdetective.TraceSegment;
import java.util.List;

/**
 * Will render the stacktrace as HTML in a pre-block with the css class 'stacktrace'.
 * If a ClassLinkResolver is set it will be used to make each line a link to some 
 * representation of that class.
 * 
 * @author johan
 */
public class HtmlTraceRenderer extends AbstractStackTraceRenderer {

    private static final String BLOCK_START = "<pre class=\"stacktrace\">";

    private static final String BLOCK_END = "</pre>";

    private static final String LINE_PAD = "  ";

    private final ClassLinkResolver classLinkResolver;

    private boolean renderLinesForIgnoredEntries = false;

    public HtmlTraceRenderer() {
        classLinkResolver = null;
    }

    public HtmlTraceRenderer(ClassLinkResolver classLinkResolver) {
        this.classLinkResolver = classLinkResolver;
    }

    public void setRenderLinesForIgnoredEntries(boolean renderLinesForIgnoredEntries) {
        this.renderLinesForIgnoredEntries = renderLinesForIgnoredEntries;
    }

    @Override
    protected void renderPreTrace(StringBuilder builder, StackTrace trace) {
        builder.append(BLOCK_START);
    }

    @Override
    protected void renderPreSegment(StringBuilder builder, TraceSegment segment) {
        builder.append(segment.getExceptionText());
        builder.append("\n");
    }

    @Override
    protected void renderEntry(StringBuilder builder, SegmentEntry entry) {
        boolean resolved = false;
        if (classLinkResolver != null) {
            String url = classLinkResolver.getLinkFor(entry);
            if (url != null) {
                resolved = true;
                renderSegmentAsLink(builder, entry, url);
            }
        }

        if (!resolved) {
            renderSegmentAsText(builder, entry);

        }
        builder.append("\n");
    }

    @Override
    protected void renderPostTrace(StringBuilder builder, StackTrace trace) {
        builder.append(BLOCK_END);
    }

    private void renderSegmentAsLink(StringBuilder builder, SegmentEntry segmentEntry, String url) {
        builder.append(LINE_PAD);
        builder.append("at ");

        builder.append("<a href=\");");
        builder.append(url);
        builder.append("\">");

        builder.append(segmentEntry.getClassName());
        builder.append(segmentEntry.getMethodName());
        builder.append("(");
        builder.append(segmentEntry.getFileName());
        builder.append(":");
        builder.append(segmentEntry.getLineNumber());
        builder.append(")");

        builder.append("</a>");
    }

    @Override
    protected void renderIgnoredEntries(StringBuilder builder, List<SegmentEntry> ignoredEntries) {
        if (renderLinesForIgnoredEntries) {
            if (ignoredEntries.size() == 1) {
                builder.append("...\n");
            } else {
                builder.append(LINE_PAD);
                builder.append("... ");
                builder.append(ignoredEntries.size());
                builder.append("...\n");
            }
        }
    }

    private void renderSegmentAsText(StringBuilder builder, SegmentEntry segmentEntry) {
        builder.append(LINE_PAD);
        builder.append(segmentEntry.getClassName());
        builder.append(segmentEntry.getMethodName());
        builder.append("(");
        builder.append(segmentEntry.getFileName());
        builder.append(":");
        builder.append(segmentEntry.getLineNumber());
        builder.append(")");
    }
}
