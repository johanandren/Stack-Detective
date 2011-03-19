package com.markatta.stackdetective.parse;

/**
 *
 * @author johan
 */
public final class StackTraceParserFactory {

    /**
     * @return The default implementation of the text parser.
     */
    public static StackTraceTextParser getDefaultTextParser() {
        return new NaiveTextParser();
    }
}
