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
