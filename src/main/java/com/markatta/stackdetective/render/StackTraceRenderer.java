
package com.markatta.stackdetective.render;

import com.markatta.stackdetective.model.StackTrace;

/**
 * A class that knows how to render a stacktrace object into a string representation.
 * 
 * @author johan
 */
public interface StackTraceRenderer {
    public String render(StackTrace stackTrace);
}
