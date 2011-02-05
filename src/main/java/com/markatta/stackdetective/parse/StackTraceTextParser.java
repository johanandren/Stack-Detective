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
     */
    public StackTrace parse(CharSequence stacktrace);
}
