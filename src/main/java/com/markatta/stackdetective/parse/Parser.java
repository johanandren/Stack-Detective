package com.markatta.stackdetective.parse;

import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.Segment;
import com.markatta.stackdetective.model.StackTrace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Takes a list of tokens and constructs an object graph with the stackdetective
 * data model out of them.
 * 
 * @author johan
 */
final class Parser {

    private enum State {

        EXCEPTION,
        ENTRY,
    }

    public StackTrace parse(List<Token> tokenList) {

        StackTrace stackTrace = new StackTrace();
        Segment currentSegment = null;

        State currentState = State.EXCEPTION;

        TokenStack tokens = new TokenStack(tokenList);

        try {
            // small state machine to handle multi-segment stack traces
            while (!tokens.isEmpty()) {
                switch (currentState) {
                    case EXCEPTION:
                        if (currentSegment != null) {
                            stackTrace.addSegment(currentSegment);
                        }

                        currentSegment = exception(tokens);

                        currentState = State.ENTRY;
                        break;

                    case ENTRY:
                        Entry entry = location(tokens);
                        if (entry != null) {
                            currentSegment.addEntry(entry);
                            // and stay at entry
                        } else {
                            currentState = State.EXCEPTION;
                        }
                }
            }
            // add last segment
            stackTrace.addSegment(currentSegment);

            return stackTrace;
        } catch (ParseException ex) {
            StringBuilder builder = new StringBuilder();
            List<Token> reversed = new ArrayList<Token>(tokens);
            Collections.reverse(reversed);
            for (Token object : reversed) {
                builder.append(object.getText());
            }
            throw new ParseException("Unparseable stacktrace at '" + builder.toString() + "'", ex);

        }
    }

    private Segment exception(TokenStack tokens) {
        // a.b.c.Class:? | : bladi bladi)?\n
        // or
        // Caused by: java.lang.ArrayIndexOutOfBoundsException: 130

        tokens.consumeWs();
        if (tokens.peek().getText().equals("Caused")) {
            tokens.popAndConcatenate(TokenType.WS, TokenType.STRING);
            tokens.popNext(true, TokenType.COLON);
            tokens.consumeWs();
        }

        String fqExceptionName = tokens.popAndConcatenateUntil(TokenType.COLON, TokenType.LINEFEED);

        String message = "";
        if (tokens.nextTypeIs(TokenType.COLON)) {
            tokens.popNext(true, TokenType.COLON); // :

            StringBuilder builder = new StringBuilder();
            tokens.consumeWs();
            message = tokens.popAndConcatenateUntil(TokenType.LINEFEED);
        }

        // consume linefeed
        tokens.consumeLfAndWs();

        return new Segment(fqExceptionName, message);
    }

    /**
     * @return Null if the current stream is not an entry
     */
    private Entry location(TokenStack tokens) {

        tokens.consumeWs();
        
        if (tokens.peek().getType() == TokenType.DOT) {
            tokens.popAndConcatenateUntil(TokenType.LINEFEED);
            tokens.consumeWs();
            return null;
        }
        
        // 'at' or '[catch]' 
        Token nextToken = tokens.popNext(true, TokenType.STRING);
        if (nextToken.getText().equals("at")) {
            // 'at' ' '  
            tokens.consumeWs();
        } else if (nextToken.getText().equals("[catch]")) {
            // '[catch]' ' ' 'at' ' '
            tokens.popNextString(true, "at");
            tokens.consumeWs();
        } else if (nextToken.getText().equals("Caused")) {
            tokens.push(nextToken);
            return null;
        } else {
            throw new ParseException("Expected an 'at' or a '[catch] but got a " + tokens.peek());
        }

        // now we should be at a.b.c.Class.method(File.type:80)
        String classAndMethodName = tokens.popAndConcatenateUntil(TokenType.PARENTHESIS_OPEN);

        int lastDot = classAndMethodName.lastIndexOf('.');
        if (lastDot == -1) {
            throw new ParseException("Expected to find at least one dot in the class/method string, found none: '" + classAndMethodName + "'");
        }
        String className = classAndMethodName.substring(0, lastDot);
        String methodName = classAndMethodName.substring(lastDot + 1);

        tokens.popNext(true, TokenType.PARENTHESIS_OPEN); // (

        String filename;
        int lineNumber = -1;
        StringBuilder builder = new StringBuilder();
        while (!tokens.nextTypeIs(TokenType.COLON, TokenType.PARENTHESIS_CLOSE)) {
            builder.append(tokens.pop().getText());
        }

        filename = builder.toString();
        nextToken = tokens.pop();

        if (nextToken.getType() == TokenType.COLON) {

            // 'FileName' '.' 'type' ':' '80' 
            lineNumber = Integer.parseInt(tokens.popNext(true, TokenType.NUMBER).getText());

        }
        tokens.popNext(true, TokenType.PARENTHESIS_CLOSE); // )


        // consume linefeed
        tokens.consumeLfAndWs();

        return new Entry(methodName, className, filename, lineNumber);
    }
}
