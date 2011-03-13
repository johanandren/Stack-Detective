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
package com.markatta.stackdetective.parse;

import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.model.Segment;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Parse a text stracktrace into stackdetective model objects
 *
 * @author johan
 */
public final class NaiveTextParser implements StackTraceTextParser {

    @Override
    public StackTrace parse(CharSequence stacktrace) {
        StringTokenizer tokenizer = new StringTokenizer(stacktrace.toString(), "\n");
        StringBuilder builder = new StringBuilder();
        List<Segment> segments = new ArrayList<Segment>();

        boolean seenAnAtYet = false;
        while (tokenizer.hasMoreTokens()) {
            String current = tokenizer.nextToken().trim();

            // ignore lines with "... 5 more"
            if (current.startsWith("... ")) {
                continue;
            }

            if (current.startsWith("[catch] at")) {
                // remove catch and handle as it was a part of this stack trace segment
                current = current.substring(7).trim();
            }

            // some stacktraces have a logger line first or multiple exception
            // message lines, consider this segment on end when we have seen another
            // line starting with "at" and reaches a line that does not
            if (!current.startsWith("at") && seenAnAtYet) {
                Segment segment = parseTraceSegment(builder);
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
        Segment segment = parseTraceSegment(builder);
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

    public Segment parseTraceSegment(CharSequence sequence) {

        String[] segmentLines = sequence.toString().split("\n");
        String exceptionText = "";
        List<Entry> entries = new ArrayList<Entry>(segmentLines.length);
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

                Entry entry = parseSegmentEntry(current);
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
        } else {
            // TODO: remove thread part?
        }
        String exceptionType = null;
        int indexOfEndOfType = exceptionText.indexOf(':');
        if (indexOfEndOfType > -1) {
            exceptionType = exceptionText.substring(0, indexOfEndOfType);
            exceptionText = exceptionText.substring(indexOfEndOfType + 1).trim();
        } else {
            exceptionType = exceptionText;
            exceptionText = "";
        }


        Segment segment = new Segment(exceptionType, exceptionText, entries);
        return segment;
    }

    /**
     * @param sequence A line with a stack trace segment entry (" at a.b.c.C.method(C.java:80)")
     * @return An entry for the row or <code>null</code> if the given sequence was not possible
     *         to parse into an entry
     */
    public Entry parseSegmentEntry(CharSequence sequence) {
        String trimmed = sequence.toString().trim();
        //  '  at a.b.c.Class.method(File.java:23)' -> 'a.b.c.Class.method(File.java:23)'
        if (trimmed.startsWith("at")) {
            trimmed = trimmed.substring(3);
        }

        // check for Class_(_File.java
        int startParenthesis = trimmed.indexOf("(");
        if (startParenthesis == -1) {
            return null;
        }


        // method(ClassFile.java:32) or method(Unknown Source)
        // -> a.b.c.Class.method
        String methodAndClass = trimmed.substring(0, startParenthesis);

        // a.b.c.Class.method into "a.b.c.Class" and "method" 
        int lastDot = methodAndClass.lastIndexOf(".");
        if (lastDot == -1) {
            throw new ParseException("Could not find last dot (between class and method) in line '" + trimmed + "'");
        }
        String className = methodAndClass.substring(0, lastDot);
        String methodName = methodAndClass.substring(lastDot + 1);


        String parenthesisContents = trimmed.substring(startParenthesis + 1, trimmed.length() - 1);
        int lineNumber = -1;
        String fileName = null;
        if (parenthesisContents.indexOf(':') > -1) {
            String[] parts = parenthesisContents.split("\\:");
            fileName = parts[0];
            lineNumber = Integer.parseInt(parts[1]);
        } else {
            // nothing right now
            fileName = parenthesisContents;
        } 

        return new Entry(methodName, className, fileName, lineNumber);

    }
}
