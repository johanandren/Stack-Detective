package com.markatta.stackdetective.util;

import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.parse.DefaultStackTraceTextParser;
import com.markatta.stackdetective.parse.StackTraceTextParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses text files into stack trace objects.
 * 
 * @author johan
 */
final class FileParser {

    static List<StackTrace> parseOnePerFile(String[] filePaths) throws IOException {
        List<StackTrace> stackTraces = new ArrayList<StackTrace>();

        for (int i = 0; i < filePaths.length; i++) {
            String path = filePaths[i];


            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            try {

                StringBuilder builder = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }

                StackTraceTextParser parser = new DefaultStackTraceTextParser();
                StackTrace trace = parser.parse(builder);
                stackTraces.add(trace);

            } finally {
                reader.close();
            }

        }
        return stackTraces;
    }
}
