package com.markatta.stackdetective;

import com.markatta.stackdetective.cost.IntelligentSubstitutionStrategy;
import com.markatta.stackdetective.render.SimpleTextRenderer;
import com.markatta.stackdetective.render.StackTraceRenderer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johan
 */
public class TestPrintRenderedApp {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: PrintFilteredApp testFile");
            System.out.println(" where testfile contains stacktraces separated by empty lines");
            return;
        }


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
            StringBuilder builder = new StringBuilder();
            String line = null;
            List<StackTrace> stackTraces = new ArrayList<StackTrace>();

            while ((line = reader.readLine()) != null) {

                if (line.trim().length() == 0) {
                    // reached empty line
                    StackTraceTextParser parser = new StackTraceTextParser();
                    StackTrace trace = parser.parse(builder);
                    stackTraces.add(trace);
                    builder.setLength(0);
                } else {
                    builder.append(line);
                    builder.append("\n");
                }

            }

            System.out.println("Found " + stackTraces.size() + " stack traces in the file " + args[0]);

            for (int i = 0; i < stackTraces.size(); i++) {
                StackTrace trace = stackTraces.get(i);

                StackTraceRenderer renderer = new SimpleTextRenderer();
                
                System.out.println("Stack trace " + i);

                System.out.println(renderer.render(trace));
            }



        } catch (Exception ex) {
            throw new RuntimeException("Error running test app", ex);
        }
    }
}
