package com.markatta.stackdetective.render;

import com.markatta.stackdetective.render.links.ClassLinkResolver;
import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.model.Segment;
import java.util.List;

/**
 * Will render the stacktrace as HTML in a pre-block with the css class 'stacktrace'.
 * If a ClassLinkResolver is set it will be used to make each line a link to some 
 * representation of that class.
 * 
 * @author johan
 */
public final class HtmlTraceRenderer extends AbstractStackTraceRenderer {

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
        boolean resolved = false;
        if (classLinkResolver != null) {
            String url = classLinkResolver.getURLFor(entry);
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

    private void renderSegmentAsLink(StringBuilder builder, Entry segmentEntry, String url) {
        builder.append(LINE_PAD);
        builder.append("at ");

        builder.append("<a href=\"");
        builder.append(url);
        builder.append("\">");

        builder.append(segmentEntry.getFqClassName());
        builder.append('.');
        builder.append(segmentEntry.getMethodName());
        builder.append('(');
        builder.append(segmentEntry.getFileName());
        builder.append(':');
        builder.append(segmentEntry.getLineNumber());
        builder.append(')');

        builder.append("</a>");
    }

    @Override
    protected void renderIgnoredEntries(StringBuilder builder, List<Entry> ignoredEntries, int indexOfFirstIgnored) {
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

    private void renderSegmentAsText(StringBuilder builder, Entry segmentEntry) {
        builder.append(LINE_PAD);
        builder.append(segmentEntry.getFqClassName());
        builder.append(segmentEntry.getMethodName());
        builder.append("(");
        builder.append(segmentEntry.getFileName());
        builder.append(":");
        builder.append(segmentEntry.getLineNumber());
        builder.append(")");
    }
}
