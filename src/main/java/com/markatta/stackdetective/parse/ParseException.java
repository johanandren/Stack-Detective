package com.markatta.stackdetective.parse;

/**
 * An error parsing a stacktrace text.
 * 
 * @author johan
 */
public final class ParseException extends RuntimeException {

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(String message) {
        super(message);
    }
}
