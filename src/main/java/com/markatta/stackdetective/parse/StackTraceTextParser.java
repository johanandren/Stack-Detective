package com.markatta.stackdetective.parse;

import com.markatta.stackdetective.SegmentEntry;
import com.markatta.stackdetective.StackTrace;
import com.markatta.stackdetective.TraceSegment;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Parse a text stracktrace into stackdetective model objects
 *
 * @author johan
 */
public final class StackTraceTextParser {

    public StackTrace parse(CharSequence stacktrace) {
        StringTokenizer tokenizer = new StringTokenizer(stacktrace.toString(), "\n");
        StringBuilder builder = new StringBuilder();
        List<TraceSegment> segments = new ArrayList<TraceSegment>();

        boolean seenAnAtYet = false;
        while (tokenizer.hasMoreTokens()) {
            String current = tokenizer.nextToken().trim();

            // ignore lines with "... 5 more"
            if (current.startsWith("... ")) {
                continue;
            }

            // some stacktraces have a logger line first or multiple exception
            // message lines, consider this segment on end when we have seen another
            // line starting with "at" and reaches a line that does not
            if (!current.startsWith("at") && seenAnAtYet) {
                TraceSegment segment = parseTraceSegment(builder);
                if (segment != null) {
                    if (builder.toString().startsWith("Caused:")) {
                        // some stack traces go backwards
                        segments.add(segment);
                    } else {
                        // and some forward
                        segments.add(0, segment);
                    }
                }


                // clear state for next segment
                builder.setLength(0);
                seenAnAtYet = false;

            } else if (!seenAnAtYet && current.startsWith("at")) {
                // we found the first "at" in this segment
                seenAnAtYet = true;
            }


            builder.append(current);
            builder.append("\n");


        }

        // we reached the end so everything up to this should be the last
        // segment
        TraceSegment segment = parseTraceSegment(builder);
        if (segment != null) {
            if (builder.toString().startsWith("Caused:")) {
                // some stack traces go backwards
                segments.add(segment);
            } else {
                // and some forward
                segments.add(0, segment);
            }
        }

        return new StackTrace(segments);
    }

    public TraceSegment parseTraceSegment(CharSequence sequence) {

        String[] segmentLines = sequence.toString().split("\n");
        String exceptionText = "";
        List<SegmentEntry> entries = new ArrayList<SegmentEntry>(segmentLines.length);
        for (String line : segmentLines) {
            String current = line.trim();
            if (!current.startsWith("at")) {
                // the segment may start with a logger entry followed by
                // the actual exception and its message
                if (exceptionText.length() > 0) {
                    exceptionText += "\n";
                }
                exceptionText += current;

            } else {

                SegmentEntry entry = parseSegmentEntry(current);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }

        if (exceptionText.startsWith("[catch]")) {
            // if we got here because the callee though that a [catch] block
            // was a segment, don't make it a segment
            return null;
        }

        // try to split up exception text into type and text
        if (exceptionText.startsWith("Caused by: ")) {
            exceptionText = exceptionText.substring(11);
        } else if (exceptionText.startsWith("Exception in thread ")) {
            // TODO: remove thread part?
        }
        String exceptionType = null;
        int indexOfEndOfType = exceptionText.indexOf(':');
        if (indexOfEndOfType > -1) {
            exceptionType = exceptionText.substring(0, indexOfEndOfType);
            exceptionText = exceptionText.substring(indexOfEndOfType + 1).trim();
        }


        TraceSegment segment = new TraceSegment(exceptionType, exceptionText, entries);
        return segment;
    }

    /**
     * @param sequence A line with a stack trace segment entry (" at a.b.c.C.method(C.java:80)")
     * @return An entry for the row or <code>null</code> if the given sequence was not possible
     *         to parse into an entry
     */
    public SegmentEntry parseSegmentEntry(CharSequence sequence) {
        String trimmed = sequence.toString().trim();
        if (trimmed.startsWith("at")) {
            trimmed = trimmed.replaceFirst("at\\s*", "");
        }

        int startParenthesis = trimmed.indexOf("(");
        if (startParenthesis == -1) {
            return null;
        }

        String methodAndClass = trimmed.substring(0, startParenthesis);

        int semicolon = trimmed.indexOf(":");
        if (semicolon == -1) {
            return null;
        }

        String fileName = trimmed.substring(startParenthesis + 1, semicolon);

        int endParenthesis = trimmed.indexOf(")");
        if (endParenthesis == -1) {
            return null;
        }

        int lineNumber = Integer.parseInt(trimmed.substring(semicolon + 1, endParenthesis));

        int lastDot = methodAndClass.lastIndexOf(".");
        String className = methodAndClass.substring(0, lastDot);
        String methodName = methodAndClass.substring(lastDot + 1);

        return new SegmentEntry(methodName, className, fileName, lineNumber);

    }
}
