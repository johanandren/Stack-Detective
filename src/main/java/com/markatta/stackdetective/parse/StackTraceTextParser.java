package com.markatta.stackdetective.parse;

import com.markatta.stackdetective.model.StackTrace;

/**
 *
 * @author johan
 */
public interface StackTraceTextParser {

    /**
     * @param stacktrace A text to parse into a stacktrace
     * @return A stack trace object or <code>null</code> if parsing was not possible.
     * @throws ParseException if the trace was not parseable
     */
    public StackTrace parse(CharSequence stacktrace);
}
