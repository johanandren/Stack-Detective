package com.markatta.stackdetective.parse;

import com.markatta.stackdetective.model.StackTrace;
import java.util.List;

/**
 * Not ready for prime time yet
 * @author johan
 */
public class DefaultTextParser implements StackTraceTextParser {

    @Override
    public StackTrace parse(CharSequence stacktrace) {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize(stacktrace.toString());
        Parser parser = new Parser();
        return parser.parse(tokens);
    }
}
