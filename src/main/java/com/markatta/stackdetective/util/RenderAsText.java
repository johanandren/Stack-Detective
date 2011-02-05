package com.markatta.stackdetective.util;

import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.render.SimpleTextRenderer;
import java.util.List;

/**
 * Small demo utility that reads stacktraces from files and prints them re-formatted
 * to standard out.
 * 
 * @author johan
 */
public final class RenderAsText {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: RenderAsText testFile1 [testFile2 ...]");
            System.out.println(" where testfile contains stacktraces separated by empty lines");
            return;
        }

        List<StackTrace> stackTraces = FileParser.parseOnePerFile(args);

        RenderUtil renderer = new RenderUtil();
        renderer.render(new SimpleTextRenderer(), stackTraces, System.out);
    }
}
