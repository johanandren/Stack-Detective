package com.markatta.stackdetective.util;

import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.render.StackTraceRenderer;
import java.io.PrintStream;
import java.util.Collection;

/**
 * Common rendering functionality for the render utils.
 * 
 * @author johan
 */
final class RenderUtil {

    private boolean printSummary = false;

    private boolean printSeparator = false;

    public RenderUtil() {
    }

    public RenderUtil(boolean printSummary, boolean printSeparator) {
        this.printSummary = printSummary;
        this.printSeparator = printSeparator;
    }
    
    void render(StackTraceRenderer renderer, Collection<StackTrace> stackTraces, PrintStream output) {
        int index = 1;
        for (StackTrace stackTrace : stackTraces) {
            if (printSummary) {
                output.println("Stack trace " + index);
            }
            output.print(renderer.render(stackTrace));
            index++;
            if (printSeparator) {
                output.println("=============================================================");
            }
        }
    }
}
